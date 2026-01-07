package com.tony.log4m.bots.commands;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.BillService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillCommand implements CommandStrategy {

    private final BillService billService;
    private final AccountService accountService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        if (Command.BUDGET == command) {
            if (param == null || param.isEmpty()) {
                return new SendMessage(chatId, "请输入预算金额");
            }
            accountService.getOrCreateDefaultAccount().setBudget(new BigDecimal(param)).updateById();
            return new SendMessage(chatId, "预算设置成功");
        }
        List<Bill> bills = fetchBillsByCommand(command, param);

        // 生成消息模板
        String template = generateTemplate(command, param, bills);

        // 生成键盘按钮
        InlineKeyboardMarkup inlineKeyboardMarkup = createKeyboardMarkup(command, param, bills);

        // 构建发送消息对象
        SendMessage sendMessage = new SendMessage(chatId, template);
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    /**
     * 根据命令查询账单列表
     */
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
                    int year = (param != null && !param.isEmpty()) ?
                            Integer.parseInt(param) : LocalDate.now().getYear();
                    String yearPrefix = String.valueOf(year);
                    yield billService.lambdaQuery()
                            .likeRight(Bill::getBillMonth, yearPrefix)
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

    /**
     * 计算账单总金额
     */
    private BigDecimal calculateTotalAmount(List<Bill> bills) {
        if (CollUtil.isEmpty(bills)) {
            return BigDecimal.ZERO;
        }

        return bills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 生成消息模板
     */
    private String generateTemplate(Command command, String param, List<Bill> bills) {
        // 计算总金额
        BigDecimal amount = calculateTotalAmount(bills);
        String description;

        switch (command) {
            case MONTH_SUMMARY_QUERY, MONTH_DETAIL_QUERY, DATE_QUERY -> description = param;
            default -> description = command.getDesc();
        }

        StringBuilder template = new StringBuilder();
        template.append(String.format("%s总计：%.2f元%n---------\n", description, amount));

        switch (command) {
            case YEAR -> {
                int year = (param != null && !param.isEmpty()) ?
                        Integer.parseInt(param) : LocalDate.now().getYear();

                // 默认视图：按月统计
                template.append("按月统计\n---------\n");

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

                template.append("\n💡 点击下方按钮切换视图");
            }
            case MONTH_SUMMARY_QUERY, LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY -> {
                // 按账单分类统计金额
                Map<String, Double> categoryMap = bills.stream()
                        .collect(Collectors.groupingBy(
                                Bill::getCategoryName,
                                Collectors.summingDouble(b -> b.getAmount().doubleValue())
                        ));

                // 如果分类为空，则替换为“未分类”
                if (categoryMap.containsKey(null) || categoryMap.containsKey("")) {
                    double unclassifiedAmount = categoryMap.getOrDefault(null, 0.0) +
                            categoryMap.getOrDefault("", 0.0);
                    categoryMap.put("未分类", unclassifiedAmount);
                    categoryMap.remove(null);
                    categoryMap.remove("");
                }

                // 找出最长分类名的长度，用于对齐
                int maxCategoryLength = categoryMap.keySet().stream()
                        .mapToInt(String::length)
                        .max()
                        .orElse(10);

                // 最小宽度是10
                int categoryLength = Math.max(maxCategoryLength, 10);

                // 按金额排序后添加到模板中
                categoryMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> {
                            String categoryName = entry.getKey();
                            int padding = Math.max(0, categoryLength * 2 - calculateDisplayWidth(categoryName));
                            template.append(String.format("%s%s  ¥%.2f\n", categoryName, " ".repeat(padding), entry.getValue()));

                        });
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

    /**
     * 创建键盘按钮
     */
    private InlineKeyboardMarkup createKeyboardMarkup(Command command, String param, List<Bill> bills) {
        switch (command) {
            case YEAR -> {
                int year = (param != null && !param.isEmpty()) ?
                        Integer.parseInt(param) : LocalDate.now().getYear();

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

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

                return markup;
            }
            case MONTH_SUMMARY_QUERY, LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY -> {
                return new InlineKeyboardMarkup();
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Bill bill : bills) {
            String billFormatted = formatBillForButton(bill);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(billFormatted);
            button.setCallbackData("bill::" + bill.getBillId());
            inlineKeyboardMarkup.addRow(button);
        }
        return inlineKeyboardMarkup;
    }


    private String formatBillForButton(Bill bill) {
        return "%s ¥%s %s %s"
                .formatted(
                        bill.getBillDate(),
                        bill.getAmount().stripTrailingZeros().toPlainString(),
                        bill.getNote(),
                        bill.getCategoryName()
                );
    }
}

