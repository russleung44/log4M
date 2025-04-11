package com.tony.log4m.bots.commands.system;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.MenuCommand;
import com.tony.log4m.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class RuleCommand implements SystemCommandStrategy {

    private final RuleService ruleService;

    @Override
    public SendMessage execute(MenuCommand menuCommand, Long chatId) {
        switch (menuCommand) {
            case RULES -> {
                return getRuleMessage(chatId);
            }
            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }

    /**
     * 获取规则列表消息（带 inline 按钮）
     */
    private SendMessage getRuleMessage(Long chatId) {
        SendMessage message = new SendMessage(chatId, "规则列表");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ruleService.lambdaQuery().list().forEach(rule -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(rule.getName());
            button.setCallbackData("rule::" + rule.getId());
            inlineKeyboardMarkup.addRow(button);
        });
        message.replyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
