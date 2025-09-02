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

    public void process(TelegramBot bot, Message message) {
        String text = message.text();
        Chat chat = message.chat();
        if (chat == null) {
            return;
        }
        Long chatId = chat.id();

        SendMessage response = Optional.ofNullable(text)
                .filter(t -> t.startsWith("/"))
                .map(t -> commandHandler.handleCommand(t, chatId))
                .orElseGet(() -> commandHandler.handleQuickRecord(text, chatId));

        Optional.ofNullable(response).ifPresent(bot::execute);
    }

}