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
import java.util.List;

/**
 * 规则命令
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
     * 添加规则
     */
    public SendMessage addRule(String[] params, Long chatId) {
        if (params.length < 3 || params.length > 4) {
            return new SendMessage(chatId, "参数错误");
        }

        // 检查参数是否为空
        for (String param : params) {
            if (param == null || param.trim().isEmpty()) {
                return new SendMessage(chatId, "参数包含空值");
            }
        }

        String keyword = params[0];
        String amountStr = params[1];
        String transactionTypeStr = params[2];

        // 验证交易类型
        if (!"0".equals(transactionTypeStr) && !"1".equals(transactionTypeStr)) {
            return new SendMessage(chatId, "交易类型参数无效");
        }

        // 解析金额并处理异常
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return new SendMessage(chatId, "金额参数无效");
        }

        TransactionType transactionType = "1".equals(transactionTypeStr)
                ? TransactionType.EXPENSE
                : TransactionType.INCOME;

        Rule rule = new Rule(keyword, amount, transactionType);

        // 处理标签
        if (params.length == 4) {
            String tagName = params[3];
            Tag tag = tagService.lambdaQuery().eq(Tag::getTagName, tagName).one();
            if (tag == null) {
                try {
                    tag = new Tag();
                    tag.setTagName(tagName).insert();
                } catch (Exception e) {
                    // 假设数据库层有唯一约束，捕获异常后重试查询
                    tag = tagService.lambdaQuery().eq(Tag::getTagName, tagName).one();
                }
            }
            rule.setTagId(tag.getTagId());
        }

        rule.insert();
        return new SendMessage(chatId, "规则添加成功");
    }

    /**
     * 获取规则列表消息（带 inline 按钮）
     */
    private SendMessage getRuleMessage(Long chatId) {
        SendMessage message = new SendMessage(chatId, "规则列表");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // 限制最多显示100条规则
        List<Rule> rules = ruleService.lambdaQuery().last("LIMIT 100").list();
        rules.forEach(rule -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(rule.getRuleName());
            button.setCallbackData("rule::" + rule.getRuleId());
            inlineKeyboardMarkup.addRow(button);
        });
        message.replyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
