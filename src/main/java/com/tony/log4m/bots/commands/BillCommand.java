package com.tony.log4m.bots.commands;

import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, lastMonth).orderByDesc(Bill::getBillDate).list();
            }
            case THIS_MONTH -> {
                String currentMonth = DateUtil.format(DateUtil.date(), "yyyyMM");
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, currentMonth).orderByDesc(Bill::getBillDate).list();
            }
        }

        // 计算总金额
        BigDecimal amount = bills.stream().map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 返回模板
        String billDetails = bills.stream()
                .map(bill -> String.format("%s %s %s %s",
                        bill.getBillDate(),
                        bill.getTransactionType().getPrefix() + bill.getAmount(),
                        bill.getNote(),
                        bill.getCategoryName()
                ))
                .collect(Collectors.joining("\n"));

        String template = """
                %s
                ---------
                当前%s总计：%.2f元
                """.formatted(
                billDetails.isEmpty() ? "暂无账单记录" : billDetails,
                command.getDesc(),
                amount.doubleValue()
        );

        return new SendMessage(chatId, template);
    }


}
