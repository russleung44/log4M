package com.tony.log4m.bots.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class MessageProcessor {

    private final CommandHandler commandHandler;
    private final com.tony.log4m.bots.core.RemarkSessionManager remarkSessionManager;
    private final com.tony.log4m.bots.core.RuleKeywordSessionManager ruleKeywordSessionManager;
    private final com.tony.log4m.service.BillService billService;
    private final com.tony.log4m.service.AccountService accountService;
    private final com.tony.log4m.service.RuleService ruleService;

    public void process(TelegramBot bot, Message message) {
        String text = message.text();
        Chat chat = message.chat();
        if (chat == null) {
            return;
        }
        Long chatId = chat.id();

        if (remarkSessionManager.isAwaitingRemark(chatId)) {
            Long billId = remarkSessionManager.consumeRemarkTarget(chatId);
            if (billId != null) {
                com.tony.log4m.models.entity.Bill bill = billService.getOptById(String.valueOf(billId)).orElseThrow();
                bill.setRemark(text);
                bill.updateById();

                java.math.BigDecimal budget = accountService.getBudget();
                String currentMonth = com.tony.log4m.utils.MoneyUtil.getMonth(java.time.LocalDate.now());
                java.math.BigDecimal monthAmount = billService.getAmountByMonth(currentMonth);
                String reply = "备注已保存\n" + com.tony.log4m.bots.core.BotUtil.getBillFormatted(bill, budget, monthAmount);
                bot.execute(new SendMessage(chatId, reply).replyMarkup(com.tony.log4m.bots.core.BotUtil.buildKeyboardMarkup("bill::" + billId)));
                return;
            }
        }

        if (ruleKeywordSessionManager.isAwaitingKeyword(chatId)) {
            Long billId = ruleKeywordSessionManager.consumeKeywordTarget(chatId);
            if (billId != null) {
                com.tony.log4m.models.entity.Bill bill = billService.getOptById(String.valueOf(billId))
                    .orElseThrow(() -> new com.tony.log4m.exception.Log4mException("该账单已被删除，无法创建规则"));

                String keyword = text.trim();
                if (cn.hutool.core.util.StrUtil.isBlank(keyword)) {
                    keyword = com.tony.log4m.service.BillService.deriveKeyword(bill);
                }
                if (cn.hutool.core.util.StrUtil.isBlank(keyword)) {
                    keyword = "规则" + bill.getBillId();
                }

                if (keyword.length() > 255) {
                    bot.execute(new SendMessage(chatId, "❌ 关键词长度不能超过255个字符"));
                    return;
                }

                com.tony.log4m.models.entity.Rule rule = ruleService.lambdaQuery()
                    .eq(com.tony.log4m.models.entity.Rule::getRuleName, keyword)
                    .last("limit 1")
                    .one();
                if (rule == null) {
                    rule = new com.tony.log4m.models.entity.Rule(keyword, bill.getAmount(), bill.getTransactionType());
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
                bot.execute(new SendMessage(chatId, details).replyMarkup(com.tony.log4m.bots.core.BotUtil.buildKeyboardMarkup("rule::" + rule.getRuleId())));
                return;
            }
        }

        SendMessage response = Optional.ofNullable(text)
                .filter(t -> t.startsWith("/"))
                .map(t -> commandHandler.handleCommand(t, chatId))
                .orElseGet(() -> commandHandler.handleQuickRecord(text, chatId));

        Optional.ofNullable(response).ifPresent(bot::execute);
    }

}