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
        BigDecimal amount = BigDecimal.ZERO;
        List<Bill> bills = new ArrayList<>();
        switch (menuCommand) {
            case TODAY -> {
                bills = billService.lambdaQuery().eq(Bill::getBillDay, DateUtil.today()).list();
                amount = billService.getAmountByDate(DateUtil.today());
            }
            case YESTERDAY -> {
                amount = billService.getAmountByDate(DateUtil.yesterday().toDateStr());
            }
            case LAST_MONTH -> {
                String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");
                amount = billService.getAmountByMonth(lastMonth);
            }
            case THIS_MONTH -> {
                String currentMonth = DateUtil.format(DateUtil.date(), "yyyyMM");
                amount = billService.getAmountByMonth(currentMonth);
            }
        }

        // 返回模板
        String billDetails = bills.stream()
                .map(bill -> String.format("日期：%s，金额：%.2f，类型：%s，备注：%s",
                        bill.getBillDay(),
                        bill.getAmount().doubleValue(),
                        bill.getTransactionType(),
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
