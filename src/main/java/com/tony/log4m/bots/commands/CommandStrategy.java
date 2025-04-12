package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;

/**
 * @author Tony
 * @since 4/11/2025
 */
public interface CommandStrategy {
    SendMessage execute(Command command, String param, Long chatId);
}

