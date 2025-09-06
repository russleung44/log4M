package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 * @since 8/15/2025
 */
@Component
@RequiredArgsConstructor
public class BudgetCommand implements CommandStrategy{
    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        return null;
    }
}
