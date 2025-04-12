package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.pojo.entity.Tag;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class RuleCommand implements CommandStrategy {

    private final TagService tagService;
    private final RuleService ruleService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        switch (command) {
            case RULES -> {
                return getRuleMessage(chatId);
            }
            case RULE_ADD -> {
                return addRule(param.split("-"), chatId);
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



    /**
     * 添加规则
     */
    public SendMessage addRule(String[] params, Long chatId) {
        // 处理 RULE 命令逻辑
        if (params.length < 3) {
            return new SendMessage(chatId, "参数错误");
        }

        String keyword = params[0];
        BigDecimal amount = new BigDecimal(params[1]);
        TransactionType transactionType = params[2].equals("1") ? TransactionType.EXPENSE : TransactionType.INCOME;

        Rule rule = new Rule(keyword, amount, transactionType);
        if (params.length > 3) {
            String tagName = params[3];
            Tag tag = tagService.lambdaQuery().eq(Tag::getName, tagName).one();
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName).insert();
            }

            rule.setTagId(tag.getId());
        }

        rule.insert();
        String replyText = "规则添加成功";
        return new SendMessage(chatId, replyText);
    }
}
