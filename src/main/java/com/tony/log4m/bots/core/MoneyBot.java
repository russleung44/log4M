package com.tony.log4m.bots.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import com.tony.log4m.bots.enums.MenuCommand;
import com.tony.log4m.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * @author TonyLeung
 * @since 2022/9/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyBot {

    private final UserService userService;
    private final CommonFunction commonFunction;
    private TelegramBot bot;
    @Value("${botToken}")
    private String botToken;

    @PostConstruct
    public void init() {
        bot = new TelegramBot(botToken);
        log.info("===init bot:{}===", botToken);
        setMyCommands();
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                log.info("receive message:{}", update);
                Message message = update.message();
                if (message != null) {
                    User from = message.from();
                    userService.saveTgUser(from.id(), from.username());
                }

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
     *设置菜单
     */
    private void setMyCommands() {
        log.info("===set commands===");
        BotCommand[] commands = Arrays.stream(MenuCommand.values())
                .map(ct -> new BotCommand(ct.getCommand(), ct.getDesc()))
                .toArray(BotCommand[]::new);

        BaseResponse response = bot.execute(new SetMyCommands(commands));
        log.info("set commands response:{}", response);
    }

}
