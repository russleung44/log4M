package com.tony.log4m.bots.handlers;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
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

    /**
     * 处理所有回调查询
     */
    public void handle(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        Long chatId = message.chat().id();
        Integer messageId = message.messageId();

        try {
            String responseText = processCallbackData(data);
            InlineKeyboardMarkup markup = BotUtil.buildKeyboardMarkup(data);

            EditMessageText editRequest = new EditMessageText(chatId, messageId, responseText)
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


    private String processCallbackData(String data) {
        String[] parts = data.split("::");
        String prefix = parts[0];
        String targetId = parts[1];

        return switch (prefix) {
            case "bill" -> buildBillDetails(targetId);
            case "rule" -> ruleService.buildRuleDetails(targetId);
            case "category" -> categoryService.buildCategoryDetails(targetId);
            case "bill_del" -> deleteBill(targetId);
            case "rule_del" -> deleteRule(targetId);
            case "category_del" -> deleteCategory(targetId);
            default -> throw new Log4mException("未知操作类型: " + prefix);
        };
    }



}