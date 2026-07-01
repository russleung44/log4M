package com.tony.log4m.bots.handlers;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.core.RemarkSessionManager;
import com.tony.log4m.bots.core.RuleKeywordSessionManager;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.models.entity.Account;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.models.entity.Rule;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.BillService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.tony.log4m.enums.TransactionType.EXPENSE;

/**
 * @author Tony
 * @since 4/10/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackProcessor {

    private final BillService billService;
    private final RuleService ruleService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final RemarkSessionManager remarkSessionManager;
    private final RuleKeywordSessionManager ruleKeywordSessionManager;

    /**
     * 处理所有回调查询
     */
    public void handle(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        Long chatId = message.chat().id();
        Integer messageId = message.messageId();

        try {
            CallbackResult result = processCallbackData(chatId, data);
            InlineKeyboardMarkup markup = result.markup != null ? result.markup : BotUtil.buildKeyboardMarkup(data);

            EditMessageText editRequest = new EditMessageText(chatId, messageId, result.text)
                    .replyMarkup(markup);
            bot.execute(editRequest);

        } catch (NoSuchElementException e) {
            log.warn("数据不存在: {}", e.getMessage());
            bot.execute(new SendMessage(chatId, "⚠️ 数据不存在或已被删除"));
        } catch (Exception e) {
            log.error("回调处理失败: {}", e.getMessage(), e);
            bot.execute(new SendMessage(chatId, "❌ 系统错误，请稍后再试"));
        }
    }


    private String buildBillDetails(String targetId) {
        Bill bill = billService.getOptById(targetId).orElseThrow();
        BigDecimal budget = accountService.getBudget();
        BigDecimal monthAmount = billService.getAmountByMonth(bill.getBillDate().toString());
        return BotUtil.getBillFormatted(bill, budget, monthAmount);
    }


    private void reverseAccountChanges(Account account, Bill bill) {
        BigDecimal amount = bill.getAmount();
        if (bill.getTransactionType() == EXPENSE) {
            account.setBalance(account.getBalance().add(amount))
                    .setConsume(account.getConsume().subtract(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount))
                    .setIncome(account.getIncome().subtract(amount));
        }
        accountService.update(account);
    }

    private String deleteRule(String ruleId) {
        Rule rule = ruleService.getOptById(ruleId).orElseThrow();
        rule.deleteById();
        return "✅ 规则已删除";
    }

    private String deleteCategory(String categoryId) {
        Category category = categoryService.getOptById(categoryId).orElseThrow();
        category.deleteById();
        return "✅ 分类已删除";
    }

    private String deleteBill(String recordId) {
        Bill bill = billService.getOptById(recordId).orElseThrow();

        Account account = accountService.getOptById(bill.getAccountId()).orElseThrow();
        reverseAccountChanges(account, bill);
        bill.deleteById();

        String currentMonth = MoneyUtil.getMonth(LocalDate.now());
        BigDecimal monthAmount = billService.getAmountByMonth(currentMonth);

        BigDecimal budget = account.getBudget();


        String template = """
                ✅ 账单删除成功
                ---------
                本月:        {}
                预算:        {}
                可用:        {}
                """;

        return StrUtil.format(
                template,
                MoneyUtil.formatBigDecimal(monthAmount),
                MoneyUtil.formatBigDecimal(budget),
                MoneyUtil.formatBigDecimal(budget.subtract(monthAmount))
        );
    }


    private CallbackResult processCallbackData(Long chatId, String data) {
        String[] parts = data.split("::");
        String prefix = parts[0];
        String targetId = parts.length > 1 ? parts[1] : null;

        return switch (prefix) {
            case "bill" -> new CallbackResult(buildBillDetails(targetId), null);
            case "rule" -> new CallbackResult(ruleService.buildRuleDetails(targetId), null);
            case "category" -> new CallbackResult(categoryService.buildCategoryDetails(targetId), null);
            case "bill_remark" -> {
                // 开始备注输入会话
                remarkSessionManager.startRemark(chatId, Long.valueOf(targetId));
                yield new CallbackResult("📝 请输入备注内容，直接回复此消息。", null);
            }
            case "bill_del" -> new CallbackResult(deleteBill(targetId), null);
            case "rule_del" -> new CallbackResult(deleteRule(targetId), null);
            case "category_del" -> new CallbackResult(deleteCategory(targetId), null);
            case "bill_rule" -> createRuleFromBill(chatId, targetId);
            case "help_rule" -> showRecentBillsForRule();
            case "help_budget" -> showBudgetMenu();
            case "help_budget_set" -> setBudgetAndSummary(targetId);
            case "help_month" -> showMonthMenu("month");
            case "help_month_detail" -> showMonthMenu("month_detail");
            case "help_date" -> showDateMenu();
            case "help_default_category" -> showCategoriesForDefault();
            case "help_set_default_category" -> setDefaultCategory(targetId);
            case "help_exec" -> handleHelpExec(parts);
            case "year_view" -> handleYearView(parts);
            default -> throw new Log4mException("未知操作类型: " + prefix);
        };
    }

    private CallbackResult showRecentBillsForRule() {
        List<Bill> bills = billService.lambdaQuery()
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getBillId)
                .last("limit 15").list();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        for (Bill b : bills) {
            String label = String.format("%s ¥%s %s %s",
                    b.getBillDate(),
                    b.getAmount().stripTrailingZeros().toPlainString(),
                    StrUtil.nullToEmpty(b.getNote()),
                    StrUtil.nullToEmpty(b.getCategoryName()));
            InlineKeyboardButton button = new InlineKeyboardButton(label).callbackData("bill_rule::" + b.getBillId());
            markup.addRow(button);
        }
        return new CallbackResult("请选择一条账单生成规则", markup);
    }

    private CallbackResult createRuleFromBill(Long chatId, String billId) {
        Bill bill = billService.getOptById(billId).orElseThrow();
        Long billIdLong = Long.valueOf(billId);
        ruleKeywordSessionManager.startKeywordInput(chatId, billIdLong);

        String suggestion = BillService.deriveKeyword(bill);
        if (StrUtil.isBlank(suggestion)) {
            suggestion = "规则" + bill.getBillId();
        }

        return new CallbackResult(
            "📝 请输入规则关键词\n\n建议关键词: " + suggestion + "\n\n直接回复此消息，输入您想要的关键词。",
            null
        );
    }

    private CallbackResult showBudgetMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        String[] options = {"1000", "1500", "2000", "2500", "3000", "4000", "5000"};
        for (String opt : options) {
            InlineKeyboardButton btn = new InlineKeyboardButton("¥" + opt).callbackData("help_budget_set::" + opt);
            markup.addRow(btn);
        }
        String text = "请选择预算金额\n或发送指令：/budget/{金额}";
        return new CallbackResult(text, markup);
    }

    private CallbackResult setBudgetAndSummary(String amountStr) {
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Account acct = accountService.getOrCreateDefaultAccount();
            acct.setBudget(amount).updateById();

            String currentMonth = MoneyUtil.getMonth(LocalDate.now());
            BigDecimal monthAmount = billService.getAmountByMonth(currentMonth);

            String template = """
                    ✅ 预算设置成功
                    ---------
                    本月:        {}
                    预算:        {}
                    可用:        {}
                    """;
            String text = StrUtil.format(
                    template,
                    MoneyUtil.formatBigDecimal(monthAmount),
                    MoneyUtil.formatBigDecimal(amount),
                    MoneyUtil.formatBigDecimal(amount.subtract(monthAmount))
            );
            return new CallbackResult(text, showBudgetMenu().markup());
        } catch (Exception e) {
            return new CallbackResult("请输入正确的金额，例如：/budget/2000", showBudgetMenu().markup());
        }
    }

    private CallbackResult showMonthMenu(String type) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dispFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 0; i < 12; i++) {
            LocalDate d = now.minusMonths(i);
            String label = d.format(dispFmt);
            String value = MoneyUtil.getMonth(d);
            InlineKeyboardButton btn = new InlineKeyboardButton(label)
                    .callbackData("help_exec::" + type + "::" + value);
            markup.addRow(btn);
        }
        String text = "请选择月份";
        return new CallbackResult(text, markup);
    }

    private CallbackResult showDateMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        LocalDate now = LocalDate.now();
        DateTimeFormatter dispFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 14; i++) {
            LocalDate d = now.minusDays(i);
            String label = d.format(dispFmt);
            InlineKeyboardButton btn = new InlineKeyboardButton(label)
                    .callbackData("help_exec::date::" + label);
            markup.addRow(btn);
        }
        String text = "请选择日期";
        return new CallbackResult(text, markup);
    }

    private CallbackResult showCategoriesForDefault() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<Category> categories = categoryService.lambdaQuery().list();
        for (Category c : categories) {
            String name = Optional.ofNullable(c.getCategoryName()).orElse("未命名");
            InlineKeyboardButton btn = new InlineKeyboardButton(name)
                    .callbackData("help_set_default_category::" + c.getCategoryId());
            markup.addRow(btn);
        }
        return new CallbackResult("请选择要设置为默认的分类", markup);
    }

    private CallbackResult setDefaultCategory(String categoryId) {
        Category category = categoryService.getOptById(categoryId).orElseThrow();
        category.setIsDefault(true).updateById();
        return new CallbackResult("✅ 默认分类设置成功：" + category.getCategoryName(), showCategoriesForDefault().markup());
    }

    private CallbackResult handleHelpExec(String[] parts) {
        String commandName = parts.length > 1 ? parts[1] : "";
        String param = parts.length > 2 ? parts[2] : "";
        Command cmd = Command.getByCommand(commandName);
        List<Bill> bills = fetchBillsByCommand(cmd, param);
        String template = generateTemplate(cmd, param, bills);
        InlineKeyboardMarkup markup = createKeyboardMarkup(cmd, bills);
        return new CallbackResult(template, markup);
    }

    private List<Bill> fetchBillsByCommand(Command command, String param) {
        try {
            return switch (command) {
                case TODAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.today())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case YESTERDAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.yesterday().toDateStr())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case LAST_MONTH, LAST_MONTH_SUMMARY -> {
                    String lastMonth = MoneyUtil.getMonth(DateUtil.lastMonth().toLocalDateTime().toLocalDate());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, lastMonth)
                            .orderByAsc(Bill::getBillDate)
                            .orderByAsc(Bill::getBillId)
                            .list();
                }
                case THIS_MONTH, THIS_MONTH_SUMMARY -> {
                    String currentMonth = MoneyUtil.getMonth(LocalDate.now());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, currentMonth)
                            .orderByAsc(Bill::getBillDate)
                            .orderByAsc(Bill::getBillId)
                            .list();
                }
                case MONTH_SUMMARY_QUERY, MONTH_DETAIL_QUERY -> billService.lambdaQuery()
                        .eq(Bill::getBillMonth, param)
                        .orderByAsc(Bill::getBillDate)
                        .orderByAsc(Bill::getBillId)
                        .list();
                case DATE_QUERY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, param)
                        .orderByAsc(Bill::getBillDate)
                        .orderByAsc(Bill::getBillId)
                        .list();
                case YEAR -> {
                    String year = (param != null && !param.isEmpty()) ? param : String.valueOf(LocalDate.now().getYear());
                    yield billService.lambdaQuery()
                            .likeRight(Bill::getBillMonth, year)
                            .orderByAsc(Bill::getBillMonth)
                            .orderByAsc(Bill::getBillDate)
                            .orderByAsc(Bill::getBillId)
                            .list();
                }
                default -> new ArrayList<>();
            };
        } catch (Exception e) {
            log.error("Error fetching bills: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private BigDecimal calculateTotalAmount(List<Bill> bills) {
        if (bills == null || bills.isEmpty()) return BigDecimal.ZERO;
        return bills.stream().map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateTemplate(Command command, String param, List<Bill> bills) {
        BigDecimal amount = calculateTotalAmount(bills);
        String description;
        switch (command) {
            case MONTH_SUMMARY_QUERY, MONTH_DETAIL_QUERY, DATE_QUERY -> description = param;
            case YEAR -> description = param + "年度";
            default -> description = command.getDesc();
        }
        StringBuilder template = new StringBuilder();
        template.append(String.format("%s总计：%.2f元%n---------%n", description, amount));
        switch (command) {
            case MONTH_SUMMARY_QUERY, LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY, YEAR -> {
                // 按月统计
                if (command == Command.YEAR) {
                    int year = Integer.parseInt(param);
                    Map<String, BigDecimal> monthlyTotals = bills.stream()
                            .collect(Collectors.groupingBy(
                                    Bill::getBillMonth,
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            Bill::getAmount,
                                            BigDecimal::add
                                    )
                            ));
                    for (int month = 1; month <= 12; month++) {
                        String monthKey = String.format("%d%02d", year, month);
                        BigDecimal monthTotal = monthlyTotals.getOrDefault(monthKey, BigDecimal.ZERO);
                        String monthName = String.format("%d年%d月", year, month);
                        int padding = Math.max(0, 12 - calculateDisplayWidth(monthName));
                        template.append(String.format("%s%s  ¥%.2f\n",
                                monthName, " ".repeat(padding), monthTotal));
                    }
                } else {
                    Map<String, Double> categoryMap = bills.stream()
                            .collect(Collectors.groupingBy(
                                    Bill::getCategoryName,
                                    Collectors.summingDouble(b -> b.getAmount().doubleValue())
                            ));
                    if (categoryMap.containsKey(null) || categoryMap.containsKey("")) {
                        double unclassifiedAmount = categoryMap.getOrDefault(null, 0.0) + categoryMap.getOrDefault("", 0.0);
                        categoryMap.put("未分类", unclassifiedAmount);
                        categoryMap.remove(null);
                        categoryMap.remove("");
                    }
                    int maxCategoryLength = categoryMap.keySet().stream().mapToInt(String::length).max().orElse(10);
                    int categoryLength = Math.max(maxCategoryLength, 10);
                    categoryMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEach(entry -> {
                                String categoryName = entry.getKey();
                                int padding = Math.max(0, categoryLength * 2 - calculateDisplayWidth(categoryName));
                                template.append(String.format("%s%s  ¥%.2f\n", categoryName, " ".repeat(padding), entry.getValue()));
                            });
                }
            }
        }
        return template.toString();
    }

    private int calculateDisplayWidth(String str) {
        if (str == null) return 0;
        int width = 0;
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
                width += 2; // 中文字符占2个字符宽度
            } else {
                width += 1; // 英文字符占1个字符宽度
            }
        }
        return width;
    }

    private InlineKeyboardMarkup createKeyboardMarkup(Command command, List<Bill> bills) {
        switch (command) {
            case MONTH_SUMMARY_QUERY, LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY, YEAR -> {
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                if (command == Command.YEAR) {
                    // 获取当前年份
                    String currentYear = bills.isEmpty() ? String.valueOf(LocalDate.now().getYear()) :
                            bills.get(0).getBillMonth().substring(0, 4);
                    int year = Integer.parseInt(currentYear);

                    // 视图切换按钮
                    markup.addRow(
                            new InlineKeyboardButton("📊 按月查看")
                                    .callbackData("year_view::" + year + "::month"),
                            new InlineKeyboardButton("📈 按分类查看")
                                    .callbackData("year_view::" + year + "::category")
                    );

                    // 年份导航
                    markup.addRow(
                            new InlineKeyboardButton("◀ " + (year - 1))
                                    .callbackData("help_exec::year::" + (year - 1)),
                            new InlineKeyboardButton((year + 1) + " ▶")
                                    .callbackData("help_exec::year::" + (year + 1))
                    );
                }
                return markup;
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Bill bill : bills) {
            String billFormatted = formatBillForButton(bill);
            InlineKeyboardButton button = new InlineKeyboardButton(billFormatted).callbackData("bill::" + bill.getBillId());
            inlineKeyboardMarkup.addRow(button);
        }
        return inlineKeyboardMarkup;
    }

    private String formatBillForButton(Bill bill) {
        return "%s ¥%s %s %s".formatted(
                bill.getBillDate(),
                bill.getAmount().stripTrailingZeros().toPlainString(),
                StrUtil.nullToEmpty(bill.getNote()),
                StrUtil.nullToEmpty(bill.getCategoryName())
        );
    }

    private CallbackResult handleYearView(String[] parts) {
        if (parts.length < 3) {
            throw new Log4mException("Invalid year view format");
        }

        String yearStr = parts[1];
        String viewType = parts[2]; // "month" or "category"
        int year = Integer.parseInt(yearStr);

        // 查询该年度的所有账单
        List<Bill> bills = billService.lambdaQuery()
                .likeRight(Bill::getBillMonth, yearStr)
                .list();

        BigDecimal total = bills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder template = new StringBuilder();
        template.append(String.format("%d年度统计总计：%.2f元%n---------%n", year, total));

        if ("month".equals(viewType)) {
            template.append(generateMonthlyView(bills, year));
        } else if ("category".equals(viewType)) {
            template.append(generateCategoryView(bills));
        }

        // 创建切换按钮
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(
                new InlineKeyboardButton("📊 按月查看")
                        .callbackData("year_view::" + year + "::month"),
                new InlineKeyboardButton("📈 按分类查看")
                        .callbackData("year_view::" + year + "::category")
        );

        // 年份导航
        markup.addRow(
                new InlineKeyboardButton("◀ " + (year - 1))
                        .callbackData("help_exec::year::" + (year - 1)),
                new InlineKeyboardButton((year + 1) + " ▶")
                        .callbackData("help_exec::year::" + (year + 1))
        );

        return new CallbackResult(template.toString(), markup);
    }

    private String generateMonthlyView(List<Bill> bills, int year) {
        StringBuilder sb = new StringBuilder("按月统计\n---------\n");

        Map<String, BigDecimal> monthlyTotals = bills.stream()
                .collect(Collectors.groupingBy(
                        Bill::getBillMonth,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Bill::getAmount,
                                BigDecimal::add
                        )
                ));

        for (int month = 1; month <= 12; month++) {
            String monthKey = String.format("%d%02d", year, month);
            BigDecimal monthTotal = monthlyTotals.getOrDefault(monthKey, BigDecimal.ZERO);
            String monthName = String.format("%d年%d月", year, month);
            int padding = Math.max(0, 12 - calculateDisplayWidth(monthName));
            sb.append(String.format("%s%s  ¥%.2f\n",
                    monthName, " ".repeat(padding), monthTotal));
        }

        return sb.toString();
    }

    private String generateCategoryView(List<Bill> bills) {
        StringBuilder sb = new StringBuilder("按分类统计\n---------\n");

        Map<String, Double> categoryMap = bills.stream()
                .collect(Collectors.groupingBy(
                        Bill::getCategoryName,
                        Collectors.summingDouble(b -> b.getAmount().doubleValue())
                ));

        // 处理空分类名称
        if (categoryMap.containsKey(null) || categoryMap.containsKey("")) {
            double unclassifiedAmount = categoryMap.getOrDefault(null, 0.0) +
                    categoryMap.getOrDefault("", 0.0);
            categoryMap.put("未分类", unclassifiedAmount);
            categoryMap.remove(null);
            categoryMap.remove("");
        }

        // 计算最大分类名长度用于对齐
        int maxCategoryLength = categoryMap.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(10);
        int categoryLength = Math.max(maxCategoryLength, 10);

        // 按金额排序并格式化
        categoryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    String categoryName = entry.getKey();
                    int padding = Math.max(0, categoryLength * 2 - calculateDisplayWidth(categoryName));
                    sb.append(String.format("%s%s  ¥%.2f\n",
                            categoryName, " ".repeat(padding), entry.getValue()));
                });

        return sb.toString();
    }

    private record CallbackResult(String text, InlineKeyboardMarkup markup) {}

}
