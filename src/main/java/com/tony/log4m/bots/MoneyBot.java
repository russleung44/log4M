package com.tony.log4m.bots;

import com.tony.log4m.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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

    @Value("${log4M_bot}")
    private String botToken;

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
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasMessage() && message.hasText()) {
            Long chatId = message.getChatId();
            String text = message.getText();
            Long userId = message.getFrom().getId();
            log.info("userId: {}, text: {}", userId, text);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            String sendText = "";
            if (text.startsWith("/")) {
                // 命令
                switch (text) {
                    case "/today" -> sendText = "今日支出: 0.00";
                    case "/yesterday" -> sendText = "昨日支出: 0.00";
                    case "/last_month" -> sendText = "上月支出: 0.00";
                    case "/current_month" -> sendText = "本月支出: 0.00";
                    default -> {
                        log.error("unknown command: {}", text);
                        return;
                    }
                }
            } else {
                // 提取消息中的金额
                String amount = getAmount(text);
                log.info("amount: {}", amount);
                sendText = "你好，你的金额是：" + amount;
            }

            sendMessage.setText(sendText);
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
            log.info("===注册机器人===");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            setMyCommands();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("TelegramApiException: {}", e.getMessage());
        }
    }

    /**
     * 设置菜单
     */
    public void setMyCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("today", "今日消费报告"));
        commands.add(new BotCommand("yesterday", "昨日消费报告"));
        commands.add(new BotCommand("last_month", "上个月消费报告"));
        commands.add(new BotCommand("current_month", "本月消费报告"));

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("TelegramApiException: {}", e.getMessage());
        }
    }


}
