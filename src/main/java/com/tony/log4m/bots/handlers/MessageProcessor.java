package com.tony.log4m.bots.handlers;

import com.pengrad.telegrambot.TelegramBot;
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
        Long chatId = message.chat().id();

        SendMessage response = Optional.of(text)
                .filter(t -> t.startsWith("/"))
                .map(cmd -> commandHandler.handleSystemCommand(cmd.substring(1), chatId))
                .orElseGet(() -> handleNonCommand(text, chatId));

        Optional.ofNullable(response).ifPresent(bot::execute);
    }

    private SendMessage handleNonCommand(String text, Long chatId) {
        return text.startsWith("@")
                ? commandHandler.handleCustomCommand(text, chatId)
                : commandHandler.handleQuickRecord(text, chatId);
    }
}