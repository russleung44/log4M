package com.tony.log4m.bots.handlers;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.BotUtil;
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
import java.util.NoSuchElementException;

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
    private final com.tony.log4m.bots.core.RemarkSessionManager remarkSessionManager;

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
            case "bill_rule" -> createRuleFromBill(targetId);
            case "help_rule" -> showRecentBillsForRule();
            default -> throw new Log4mException("未知操作类型: " + prefix);
        };
    }

    private CallbackResult showRecentBillsForRule() {
        java.util.List<Bill> bills = billService.lambdaQuery()
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

    private CallbackResult createRuleFromBill(String billId) {
        Bill bill = billService.getOptById(billId).orElseThrow();

        String keyword = deriveKeyword(bill);
        if (StrUtil.isBlank(keyword)) {
            keyword = "规则" + bill.getBillId();
        }

        // 如果存在同名规则则更新，否则创建
        Rule rule = ruleService.lambdaQuery().eq(Rule::getRuleName, keyword).last("limit 1").one();
        if (rule == null) {
            rule = new Rule(keyword, bill.getAmount(), bill.getTransactionType());
            rule.setCategoryId(bill.getCategoryId());
            rule.insert();
        } else {
            rule.setAmount(bill.getAmount());
            rule.setTransactionType(bill.getTransactionType());
            if (bill.getCategoryId() != null) {
                rule.setCategoryId(bill.getCategoryId());
            }
            rule.updateById();
        }

        String details = ruleService.buildRuleDetails(rule);
        return new CallbackResult(details, BotUtil.buildKeyboardMarkup("rule::" + rule.getRuleId()));
    }

    private String deriveKeyword(Bill bill) {
        if (StrUtil.isNotBlank(bill.getNote())) return bill.getNote();
        if (StrUtil.isNotBlank(bill.getRemark())) return bill.getRemark();
        if (StrUtil.isNotBlank(bill.getCategoryName())) return bill.getCategoryName();
        return null;
    }

    private record CallbackResult(String text, InlineKeyboardMarkup markup) {}


}