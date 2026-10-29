package com.tony.log4m.task;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.CommandHandler;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.core.MoneyBot;
import com.tony.log4m.convert.CategoryConvert;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.models.entity.BillSchedule;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.BillScheduleService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 定时记账任务：每分钟检查到点的计划并记账
 *
 * @author Tony
 * @since 10/29/2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillScheduleJob {

    private final BillScheduleService billScheduleService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final CommandHandler commandHandler;

    // shortcut: 只在触发时刻整点运行，停机或 fire 阻塞跨过时刻的计划不补记，需要补记时再加补偿
    @Scheduled(cron = "0 * * * * *")
    public void run() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        for (BillSchedule schedule : billScheduleService.list()) {
            if (!billScheduleService.isDue(schedule, today, now)) {
                continue;
            }
            try {
                fire(schedule);
            } catch (Exception e) {
                log.error("定时记账失败: scheduleId={}", schedule.getScheduleId(), e);
            }
        }
    }

    private void fire(BillSchedule schedule) {
        Category category = categoryService.getOptById(schedule.getCategoryId())
                .orElseGet(() -> categoryService.getOrCreate(schedule.getCategoryName()));

        Bill bill = Bill.builder()
                .billDate(LocalDate.now())
                .billMonth(MoneyUtil.getMonth(LocalDate.now()))
                .transactionType(TransactionType.EXPENSE)
                .amount(schedule.getAmount())
                .note(schedule.getNote())
                .accountId(accountService.getOrCreateDefaultAccount().getAccountId())
                .build();
        CategoryConvert.INSTANCE.updateBill(bill, category);
        bill.insert();

        String reply = "⏰ 定时记账\n" + commandHandler.buildBillText(bill);
        SendMessage message = new SendMessage(schedule.getChatId(), reply)
                .replyMarkup(BotUtil.buildKeyboardMarkup("bill::" + bill.getBillId()));
        MoneyBot.getBot().execute(message);
        log.info("定时记账完成: scheduleId={}, billId={}", schedule.getScheduleId(), bill.getBillId());
    }
}
