package com.tony.log4m.bots;

import com.tony.log4m.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TonyLeung
 * @date 2022/9/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyBot extends TelegramLongPollingBot {

    private final RuleService ruleService;

    /**
     * 获取第一个金额
     *
     * @param text 文本
     * @return 金额, 可负数, 保留两位小数
     */
    public static String getAmount(String text) {
        Pattern integerPattern = Pattern.compile("-?\\d+(\\.\\d{1,2})?");
        Matcher matcher = integerPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    public String getBotUsername() {
        return "log4M_bot";
    }

    @Override
    public String getBotToken() {
        return "5649248028:AAH5rptB734zVFydAA25ILxD8wYCqQ6cUd8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasMessage() && message.hasText()) {
            Long chatId = message.getChatId();
            String text = message.getText();
            Long userId = message.getFrom().getId();
            log.info("userId: {}, text: {}", userId, text);
            // 提取消息中的金额
            String amount = getAmount(text);
            log.info("amount: {}", amount);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("你好，你的金额是：" + amount);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("TelegramApiException: {}", e.getMessage());
            }
        }
    }

    /**
     * 启动注册机器人
     */
    @PostConstruct
    public void start() {
        try {
            log.info("注册机器人");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MoneyBot(ruleService));
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("TelegramApiException: {}", e.getMessage());
        }
    }
}
