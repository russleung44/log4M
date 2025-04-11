package com.tony.log4m.bots;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.pojo.entity.Tag;
import com.tony.log4m.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Tony
 * @since 4/10/2025
 */
@Component
@RequiredArgsConstructor
public class RuleCustomCommand implements CustomCommandStrategy {

    private final TagService tagService;

    @Override
    public SendMessage execute(String[] params, Long chatId, Long userId) {
        // 处理 RULE 命令逻辑
        if (params.length < 3) {
            return new SendMessage(chatId, "参数错误");
        }

        String keyword = params[0];
        BigDecimal amount = new BigDecimal(params[1]);
        TransactionType transactionType = params[2].equals("1") ? TransactionType.EXPENSE : TransactionType.INCOME;

        Rule rule = new Rule(keyword, amount, transactionType).setUserId(userId);
        if (params.length > 3) {
            String tagName = params[3];
            Tag tag = tagService.lambdaQuery().eq(Tag::getName, tagName).one();
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName).setUserId(userId).insert();
            }

            rule.setTagId(tag.getId());
        }

        rule.insert();
        String replyText = "规则添加成功";
        return new SendMessage(chatId, replyText);
    }
}
