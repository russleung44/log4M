package com.tony.log4m.bots.core;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.tony.log4m.pojo.entity.Bill;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author Tony
 * @since 4/18/2025
 */
@Slf4j
public class BotUtil {

    public static InlineKeyboardMarkup createButton(String text, String callbackPrefix, Serializable targetId) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text).callbackData(callbackPrefix + "::" + targetId);
        return new InlineKeyboardMarkup(inlineKeyboardButton);
    }

    public static String getBillFormatted(Bill bill) {
        String template = """
                账单详情
                ---------
                金额:        {}
                日期:        {}
                备注:        {}
                分类:        {}
                """;

        return StrUtil.format(
                template,
                bill.getAmount().stripTrailingZeros().toPlainString(),
                bill.getBillDate(),
                bill.getNote(),
                bill.getCategoryName()
        );
    }

    public static InlineKeyboardMarkup buildKeyboardMarkup(String data) {
        // 输入校验
        if (data == null || !data.contains("::")) {
            throw new IllegalArgumentException("Invalid input format: expected 'prefix::targetId'");
        }

        String[] parts = data.split("::", 2); // 确保只分割两次，避免过多部分
        String prefix = parts[0];
        String targetId = parts.length > 1 ? parts[1] : "";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton button = switch (prefix) {
            case "bill" -> createButton("❌ 删除记录", "bill_del", targetId);
            case "rule" -> createButton("❌ 删除规则", "rule_del", targetId);
            case "category" -> createButton("❌ 删除分类", "category_del", targetId);
            default -> null;
        };

        if (button != null) {
            inlineKeyboardMarkup.addRow(button);
        }

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardButton createButton(String text, String callbackPrefix, String targetId) {
        return new InlineKeyboardButton(text).callbackData(callbackPrefix + "::" + targetId);
    }

}
