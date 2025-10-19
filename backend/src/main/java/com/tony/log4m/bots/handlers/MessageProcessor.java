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
    private final com.tony.log4m.service.BillService billService;
    private final com.tony.log4m.service.AccountService accountService;

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
                bill.setNote(text);
                bill.updateById();

                java.math.BigDecimal budget = accountService.getBudget();
                String currentMonth = com.tony.log4m.utils.MoneyUtil.getMonth(java.time.LocalDate.now());
                java.math.BigDecimal monthAmount = billService.getAmountByMonth(currentMonth);
                String reply = "备注已保存\n" + com.tony.log4m.bots.core.BotUtil.getBillFormatted(bill, budget, monthAmount);
                bot.execute(new SendMessage(chatId, reply).replyMarkup(com.tony.log4m.bots.core.BotUtil.buildKeyboardMarkup("bill::" + billId)));
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