package com.tony.log4m.bots;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author TonyLeung
 * @since 2022/9/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyBot {


    @Value("${botToken}")
    private String botToken;

    private final CommonFunction commonFunction;


    @PostConstruct
    public void init() {
        TelegramBot bot = new TelegramBot(botToken);
        log.info("===init bot===");
        bot.setUpdatesListener(updates -> {
            log.info("收到消息:{}", updates);
            updates.forEach(update -> {
                commonFunction.mainFunc(bot, update);
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                log.error("init bot error", e);
            }
        });
    }

    /*
     */
/**
 * 设置菜单
 *//*

    private void setMyCommands() throws TelegramApiException {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("today", "今日消费报告"));
        commands.add(new BotCommand("yesterday", "昨日消费报告"));
        commands.add(new BotCommand("last_month", "上个月消费报告"));
        commands.add(new BotCommand("current_month", "本月消费报告"));
        commands.add(new BotCommand("rule", "规则"));

        SetMyCommands setMyCommands = new SetMyCommands(commands);
        telegramClient.execute(setMyCommands);
    }
*/


}
