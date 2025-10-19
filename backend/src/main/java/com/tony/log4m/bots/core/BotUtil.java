package com.tony.log4m.bots.core;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.utils.MoneyUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

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

    public static String getBillFormatted(Bill bill, BigDecimal budget, BigDecimal monthAmount) {
        String template = """
                账单详情
                ---------
                金额:        {}
                日期:        {}
                备注:        {}
                分类:        {}
                本月:        {}
                预算:        {}
                可用:        {}
                """;

        return StrUtil.format(
                template,
                MoneyUtil.formatBigDecimal(bill.getAmount()),
                bill.getBillDate(),
                bill.getNote(),
                bill.getCategoryName(),
                MoneyUtil.formatBigDecimal(monthAmount),
                MoneyUtil.formatBigDecimal(budget),
                MoneyUtil.formatBigDecimal(budget.subtract(monthAmount))
        );
    }


    public static InlineKeyboardMarkup buildKeyboardMarkup(String data) {
        // 输入校验
        if (data == null || !data.contains("::")) {
            throw new Log4mException("Invalid input format: expected 'prefix::targetId'");
        }

        String[] parts = data.split("::", 2); // 确保只分割两次，避免过多部分
        String prefix = parts[0];
        String targetId = parts.length > 1 ? parts[1] : "";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        switch (prefix) {
            case "bill" -> {
                InlineKeyboardButton remarkButton = createButton("📝 备注", "bill_note", targetId);
                InlineKeyboardButton deleteButton = createButton("❌ 删除记录", "bill_del", targetId);
                inlineKeyboardMarkup.addRow(remarkButton, deleteButton);
            }
            case "rule" -> inlineKeyboardMarkup.addRow(createButton("❌ 删除规则", "rule_del", targetId));
            case "category" -> inlineKeyboardMarkup.addRow(createButton("❌ 删除分类", "category_del", targetId));
            default -> {
                // no-op
            }
        }

        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardButton createButton(String text, String callbackPrefix, String targetId) {
        return new InlineKeyboardButton(text).callbackData(callbackPrefix + "::" + targetId);
    }

}
