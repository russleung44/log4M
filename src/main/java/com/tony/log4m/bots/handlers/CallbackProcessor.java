package com.tony.log4m.bots.handlers;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
    private final TagService tagService;
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
            String responseText = processCallbackData(data, chatId);
            InlineKeyboardMarkup markup = buildKeyboardMarkup(data);

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

    private String processCallbackData(String data, Long chatId) {
        String[] parts = data.split("::");
        String action = parts[0];
        String targetId = parts[1];

        return switch (action) {
            case "rule" -> buildRuleDetails(targetId);
            case "record" -> handleRecordAction(targetId, chatId);
            case "rule_del" -> deleteRule(targetId);
            default -> throw new IllegalArgumentException("未知操作类型: " + action);
        };
    }

    private String buildRuleDetails(String ruleId) {
        Rule rule = ruleService.getOptById(ruleId).orElseThrow();

        return StrUtil.format("""
                        规则详情
                        名称: {}
                        关键词: {}
                        分类: {}
                        标签: {}
                        类型: {}
                        金额: {}
                        """,
                rule.getName(),
                rule.getKeywords(),
                getCategoryName(rule.getCategoryId()),
                getTagName(rule.getTagId()),
                rule.getTransactionType().getType(),
                rule.getAmount());
    }

    private String handleRecordAction(String recordId, Long chatId) {
        Bill bill = billService.getOptById(recordId).orElseThrow();

        Account account = accountService.getOptById(bill.getAccountId()).orElseThrow();

        reverseAccountChanges(account, bill);
        bill.deleteById();

        return "✅ 记录删除成功\n"
                + "账户: " + account.getName() + "\n"
                + "余额: " + account.getBalance();
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

    private InlineKeyboardMarkup buildKeyboardMarkup(String data) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (data.startsWith("rule::")) {
            InlineKeyboardButton deleteBtn = new InlineKeyboardButton("❌ 删除规则")
                    .callbackData("rule_del::" + data.split("::")[1]);
            inlineKeyboardMarkup.addRow(deleteBtn);
        }
        return inlineKeyboardMarkup;
    }

    private String getCategoryName(Long categoryId) {
        return categoryService.getOptById(categoryId)
                .map(Category::getName)
                .orElse("未分类");
    }

    private String getTagName(Long tagId) {
        return tagService.getOptById(tagId)
                .map(Tag::getName)
                .orElse("无标签");
    }
}