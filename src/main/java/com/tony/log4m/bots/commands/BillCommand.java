package com.tony.log4m.bots.commands;

import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class BillCommand implements CommandStrategy {

    private final BillService billService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        List<Bill> bills = new ArrayList<>();
        switch (command) {
            case TODAY -> {
                bills = billService.lambdaQuery().eq(Bill::getBillDate, DateUtil.today()).orderByDesc(Bill::getBillDate).list();
            }
            case YESTERDAY -> {
                bills = billService.lambdaQuery().eq(Bill::getBillDate, DateUtil.yesterday().toDateStr()).orderByDesc(Bill::getBillDate).list();
            }
            case LAST_MONTH -> {
                String lastMonth = MoneyUtil.getMonth(DateUtil.lastMonth().toLocalDateTime().toLocalDate());
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, lastMonth).orderByDesc(Bill::getBillDate).list();
            }
            case THIS_MONTH -> {
                String currentMonth = MoneyUtil.getMonth(LocalDate.now());
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, currentMonth).orderByDesc(Bill::getBillDate).list();
            }
        }

        // 计算总金额
        BigDecimal amount = bills.stream().map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        String template = """
                ---------
                %s总计：%.2f元
                """.formatted(
                command.getDesc(),
                amount.doubleValue()
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        bills.forEach(bill -> {
            String billFormatted = String.format("%s %s %s %s",
                    bill.getBillDate(),
                    bill.getTransactionType().getPrefix() + bill.getAmount(),
                    bill.getNote(),
                    bill.getCategoryName());
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(billFormatted);
            button.setCallbackData("bill::" + bill.getBillId());
            inlineKeyboardMarkup.addRow(button);
        });

        SendMessage sendMessage = new SendMessage(chatId, template);
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }


}
