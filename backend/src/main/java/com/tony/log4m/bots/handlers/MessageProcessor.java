package com.tony.log4m.bots.handlers;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.CommandHandler;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.core.RemarkSessionManager;
import com.tony.log4m.bots.core.RuleKeywordSessionManager;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.models.entity.Rule;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.BillService;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class MessageProcessor {

    private final CommandHandler commandHandler;
    private final RemarkSessionManager remarkSessionManager;
    private final RuleKeywordSessionManager ruleKeywordSessionManager;
    private final BillService billService;
    private final AccountService accountService;
    private final RuleService ruleService;

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
                Bill bill = billService.getOptById(String.valueOf(billId)).orElseThrow();
                bill.setRemark(text);
                bill.updateById();

                BigDecimal budget = accountService.getBudget();
                String currentMonth = MoneyUtil.getMonth(LocalDate.now());
                BigDecimal monthAmount = billService.getAmountByMonth(currentMonth);
                String reply = "备注已保存\n" + BotUtil.getBillFormatted(bill, budget, monthAmount);
                bot.execute(new SendMessage(chatId, reply).replyMarkup(BotUtil.buildKeyboardMarkup("bill::" + billId)));
                return;
            }
        }

        if (ruleKeywordSessionManager.isAwaitingKeyword(chatId)) {
            Long billId = ruleKeywordSessionManager.consumeKeywordTarget(chatId);
            if (billId != null) {
                Bill bill = billService.getOptById(String.valueOf(billId))
                    .orElseThrow(() -> new Log4mException("该账单已被删除，无法创建规则"));

                // ruleName 始终使用账单的原备注
                String ruleName = BillService.deriveKeyword(bill);
                if (StrUtil.isBlank(ruleName)) {
                    ruleName = "规则" + bill.getBillId();
                }

                // keywords 使用用户输入的关键词
                String keywords = text.trim();
                if (StrUtil.isBlank(keywords)) {
                    keywords = ruleName;
                }

                if (keywords.length() > 255) {
                    bot.execute(new SendMessage(chatId, "❌ 关键词长度不能超过255个字符"));
                    return;
                }

                Rule rule = ruleService.lambdaQuery()
                    .eq(Rule::getRuleName, ruleName)
                    .last("limit 1")
                    .one();
                if (rule == null) {
                    rule = new Rule();
                    rule.setRuleName(ruleName);
                    rule.setKeywords(keywords);
                    rule.setAmount(bill.getAmount());
                    rule.setTransactionType(bill.getTransactionType());
                    rule.setCategoryId(bill.getCategoryId());
                    rule.insert();
                } else {
                    rule.setAmount(bill.getAmount());
                    rule.setTransactionType(bill.getTransactionType());
                    rule.setKeywords(keywords);
                    if (bill.getCategoryId() != null) {
                        rule.setCategoryId(bill.getCategoryId());
                    }
                    rule.updateById();
                }

                String details = ruleService.buildRuleDetails(rule);
                bot.execute(new SendMessage(chatId, details).replyMarkup(BotUtil.buildKeyboardMarkup("rule::" + rule.getRuleId())));
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
