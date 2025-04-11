package com.tony.log4m.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.pojo.entity.User;
import com.tony.log4m.service.UserService;
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

    private final UserService userService;
    private final CommandHandler commandHandler;

    public void process(TelegramBot bot, Message message) {
        String text = message.text();
        Long chatId = message.chat().id();
        User user = userService.getByTgUserId(message.from().id());

        Long userId = user.getId();
        SendMessage response = Optional.of(text)
                .filter(t -> t.startsWith("/"))
                .map(cmd -> commandHandler.handleSystemCommand(cmd.substring(1), chatId, userId))
                .orElseGet(() -> handleNonCommand(text, chatId, userId));

        Optional.ofNullable(response).ifPresent(bot::execute);
    }

    private SendMessage handleNonCommand(String text, Long chatId, Long userId) {
        return text.startsWith("@")
                ? commandHandler.handleCustomCommand(text, chatId, userId)
                : commandHandler.handleQuickRecord(text, chatId, userId);
    }
}