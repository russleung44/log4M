package com.tony.log4m.bots.core;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.handlers.CallbackProcessor;
import com.tony.log4m.bots.handlers.FileHandler;
import com.tony.log4m.bots.handlers.MessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author TonyLeung
 * @since 2024/5/5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonFunction {

    private final MessageProcessor messageProcessor;
    private final CallbackProcessor callbackProcessor;
    private final FileHandler fileHandler;

    public void mainFunc(TelegramBot bot, Update update) {
        try {
            processUpdate(bot, update);
        } catch (RuntimeException e) {
            log.error("RuntimeException: {}", e.getMessage(), e);
            bot.execute(new SendMessage(getChatId(update), e.getMessage()));
        } catch (Exception e) {
            log.error("未处理异常: {}", e.getMessage(), e);
            bot.execute(new SendMessage(getChatId(update), "系统内部错误"));
        }
    }

    private void processUpdate(TelegramBot bot, Update update) {
        if (update.message() != null) {
            handleMessage(bot, update.message());
        }
        if (update.callbackQuery() != null) {
            callbackProcessor.handle(bot, update.callbackQuery());
        }
    }

    private void handleMessage(TelegramBot bot, Message message) {
        Optional.ofNullable(message.document())
                .ifPresent(doc -> fileHandler.handle(bot, message));

        Optional.ofNullable(message.text())
                .filter(StrUtil::isNotBlank)
                .ifPresent(text -> messageProcessor.process(bot, message));
    }

    private Long getChatId(Update update) {
        return Optional.ofNullable(update.message())
                .map(msg -> msg.chat().id())
                .orElseGet(() -> update.callbackQuery().message().chat().id());
    }


}
