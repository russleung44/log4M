package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.models.entity.BillSchedule;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.service.BillScheduleService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时记账命令
 *
 * @author Tony
 * @since 10/29/2026
 */
@Component
@RequiredArgsConstructor
public class ScheduleCommand implements CommandStrategy {

    private static final String USAGE = "格式：/cron_add/{HH:mm}-{周几}-{金额}-{分类}-{备注}\n例：/cron_add/09:00-12345-7-吔-鸡蛋肠";

    private final BillScheduleService billScheduleService;
    private final CategoryService categoryService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        return switch (command) {
            case CRONS -> listSchedules(chatId);
            case CRON_ADD -> addSchedule(param.split("-", 5), chatId);
            default -> new SendMessage(chatId, "未识别的命令");
        };
    }

    /**
     * 添加定时记账
     * 格式：/cron_add/{HH:mm}-{周几}-{金额}-{分类}-{备注}，周几1=周一…7=周日
     */
    private SendMessage addSchedule(String[] params, Long chatId) {
        if (params.length < 5) {
            return new SendMessage(chatId, "参数错误，" + USAGE);
        }

        LocalTime execTime;
        try {
            execTime = LocalTime.parse(params[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return new SendMessage(chatId, "时间格式无效，应为 HH:mm，如 09:00");
        }

        String weekDays = params[1].trim();
        if (!weekDays.matches("[1-7]{1,7}")) {
            return new SendMessage(chatId, "周几无效，应为1~7的数字（1=周一…7=周日），如 12345 表示工作日");
        }

        String amountStr = params[2].trim();
        if (!amountStr.matches("\\d{1,8}(\\.\\d{1,2})?")) {
            return new SendMessage(chatId, "金额参数无效，应为正数且最多两位小数");
        }
        BigDecimal amount = new BigDecimal(amountStr);

        String categoryName = params[3].trim();
        String note = params[4].trim();
        if (categoryName.isEmpty() || note.isEmpty()) {
            return new SendMessage(chatId, "分类和备注不能为空");
        }

        Category category = categoryService.getOrCreate(categoryName);

        BillSchedule schedule = BillSchedule.builder()
                .chatId(chatId)
                .note(note)
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .amount(amount)
                .execTime(execTime)
                .weekDays(weekDays)
                .enabled(true)
                .build();
        schedule.insert();
        return new SendMessage(chatId, "✅ 定时记账已添加：" + describe(schedule));
    }

    private SendMessage listSchedules(Long chatId) {
        List<BillSchedule> schedules = billScheduleService.lambdaQuery()
                .eq(BillSchedule::getChatId, chatId)
                .orderByAsc(BillSchedule::getExecTime)
                .list();
        if (schedules.isEmpty()) {
            return new SendMessage(chatId, "暂无定时记账\n使用如下命令添加:\n/" + USAGE);
        }

        String text = "⏰ 定时记账（共" + schedules.size() + "条）\n" +
                schedules.stream().map(this::describe).collect(Collectors.joining("\n"));
        SendMessage message = new SendMessage(chatId, text);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        schedules.forEach(s -> markup.addRow(new InlineKeyboardButton("❌ 删除: " + s.getNote())
                .callbackData("cron_del::" + s.getScheduleId())));
        message.replyMarkup(markup);
        return message;
    }

    private String describe(BillSchedule s) {
        return String.format("%s  %s  ¥%s  #%s  %s",
                s.getExecTime(),
                describeDays(s.getWeekDays()),
                MoneyUtil.formatBigDecimal(s.getAmount()),
                s.getCategoryName(),
                s.getNote());
    }

    private String describeDays(String days) {
        if ("12345".equals(days)) return "工作日";
        if ("1234567".equals(days)) return "每天";
        return days.chars()
                .mapToObj(d -> "周" + "一二三四五六日".charAt(d - '1'))
                .collect(Collectors.joining());
    }
}
