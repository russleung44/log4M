package com.tony.log4m.bots.commands.system;

import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.MenuCommand;
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
public class BillCommand implements SystemCommandStrategy {

    private final BillService billService;

    @Override
    public SendMessage execute(MenuCommand menuCommand, Long chatId) {
        List<Bill> bills = new ArrayList<>();
        switch (menuCommand) {
            case TODAY -> {
                bills = billService.lambdaQuery().eq(Bill::getBillDay, DateUtil.today()).list();
            }
            case YESTERDAY -> {
                bills = billService.lambdaQuery().eq(Bill::getBillDay, DateUtil.yesterday().toDateStr()).list();
            }
            case LAST_MONTH -> {
                String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, lastMonth).list();
            }
            case THIS_MONTH -> {
                String currentMonth = DateUtil.format(DateUtil.date(), "yyyyMM");
                bills = billService.lambdaQuery().eq(Bill::getBillMonth, currentMonth).list();
            }
        }

        // 计算总金额
        BigDecimal amount = bills.stream().map(Bill::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // 返回模板
        String billDetails = bills.stream()
                .map(bill -> String.format("%s，%s，%s, %s",
                        bill.getBillDay(),
                        bill.getTransactionType().getPrefix() + bill.getAmount(),
                        bill.getCategoryName(),
                        bill.getNote()))
                .collect(Collectors.joining("\n"));

        String template = """
                %s
                ---------
                当前%s总计：%.2f元
                """.formatted(
                billDetails.isEmpty() ? "暂无账单记录" : billDetails,
                menuCommand.getDesc(),
                amount.doubleValue()
        );

        return new SendMessage(chatId, template);
    }


}
