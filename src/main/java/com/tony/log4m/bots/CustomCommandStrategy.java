package com.tony.log4m.bots;

import com.pengrad.telegrambot.request.SendMessage;

/**
 * @author Tony
 * @since 4/11/2025
 */
public interface CustomCommandStrategy {
    SendMessage execute(String[] params, Long chatId, Long userId);
}

