package com.tony.log4m.bots;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * @author TonyLeung
 * @since 2022/9/27
 */
@Slf4j
@Component
public class MoneyBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {


    private final TelegramClient telegramClient;
    private final CommonFunction commonFunction;
    @Value("${botToken}")
    private String botToken;

    public MoneyBot(@Value("${botToken}") String botToken, CommonFunction commonFunction) {
        telegramClient = new OkHttpTelegramClient(botToken);
        this.commonFunction = commonFunction;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("===写入菜单===");
            setMyCommands();
        } catch (TelegramApiException e) {
            log.error("写入菜单失败: {}", e.getLocalizedMessage());
        }
    }

    /**
     * 设置菜单
     */
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


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {
        commonFunction.mainFunc(telegramClient, update);
    }
}
