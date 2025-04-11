package com.tony.log4m.bots;

import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.enums.MenuCommand;
import com.tony.log4m.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
        switch (menuCommand) {
            case TODAY -> {
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

        String commandAnswer = menuCommand.getDesc() + ": " + amount;
        return new SendMessage(chatId, commandAnswer);
    }


}
