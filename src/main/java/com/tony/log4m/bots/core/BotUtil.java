package com.tony.log4m.bots.core;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.tony.log4m.pojo.entity.Bill;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tony
 * @since 4/18/2025
 */
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

        BigDecimal amount = bill.getAmount();
        String amountPrefix = bill.getTransactionType().getPrefix();

        return StrUtil.format(
                template,
                amountPrefix + amount,
                bill.getBillDate(),
                bill.getNote(),
                bill.getCategoryName()
        );
    }
}
