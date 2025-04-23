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
        List<Bill> bills = fetchBillsByCommand(command);

        // 计算总金额
        BigDecimal amount = calculateTotalAmount(bills);

        // 生成消息模板
        String template = generateTemplate(command, amount);

        // 生成键盘按钮
        InlineKeyboardMarkup inlineKeyboardMarkup = createKeyboardMarkup(bills);

        // 构建发送消息对象
        SendMessage sendMessage = new SendMessage(chatId, template);
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    /**
     * 根据命令查询账单列表
     */
    private List<Bill> fetchBillsByCommand(Command command) {
        try {
            return switch (command) {
                case TODAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.today())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case YESTERDAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.yesterday().toDateStr())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case LAST_MONTH -> {
                    String lastMonth = MoneyUtil.getMonth(DateUtil.lastMonth().toLocalDateTime().toLocalDate());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, lastMonth)
                            .orderByDesc(Bill::getBillDate)
                            .orderByDesc(Bill::getBillId)
                            .list();
                }
                case THIS_MONTH -> {
                    String currentMonth = MoneyUtil.getMonth(LocalDate.now());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, currentMonth)
                            .orderByDesc(Bill::getBillDate)
                            .orderByDesc(Bill::getBillId)
                            .list();
                }
                default -> new ArrayList<>();
            };
        } catch (Exception e) {
            // 异常处理：记录日志并返回空列表
            System.err.println("Error fetching bills: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 计算账单总金额
     */
    private BigDecimal calculateTotalAmount(List<Bill> bills) {
        return bills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 生成消息模板
     */
    private String generateTemplate(Command command, BigDecimal amount) {
        String description = command.getDesc();
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            description = "无账单记录";
        }
        return """
                %s总计：%.2f元
                ---------
                """.formatted(description, amount);
    }

    /**
     * 创建键盘按钮
     */
    private InlineKeyboardMarkup createKeyboardMarkup(List<Bill> bills) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Bill bill : bills) {
            String billFormatted = String.format("%s ¥%s %s %s",
                    bill.getBillDate(),
                    bill.getAmount().stripTrailingZeros().toPlainString(),
                    bill.getNote(),
                    bill.getCategoryName());
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(billFormatted);
            button.setCallbackData("bill::" + bill.getBillId());
            inlineKeyboardMarkup.addRow(button);
        }
        return inlineKeyboardMarkup;
    }
}

