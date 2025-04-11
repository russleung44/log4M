package com.tony.log4m.bots;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.enums.MenuCommand;

/**
 * @author Tony
 * @since 4/11/2025
 */
public interface SystemCommandStrategy {
    SendMessage execute(MenuCommand menuCommand, Long chatId, Long userId);
}

