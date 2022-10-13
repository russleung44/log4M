package com.tony.log4m.bots;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author TonyLeung
 * @date 2022/9/27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyBot extends TelegramLongPollingBot {

    private final TagService tagService;
    private final RuleService ruleService;
    private final UserService userService;
    private final RecordService recordService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @Value("${log4M_bot}")
    private String botToken;

    private SendMessage getRuleMessage(Long chatId, User user) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("规则列表")
                .build();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        ruleService.query().eq("user_id", user.getId()).list().forEach(rule -> {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(rule.getName())
                    .callbackData("rule::" + rule.getId())
                    .build();

            buttons.add(button);
        });
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(buttons);
        message.setReplyMarkup(new InlineKeyboardMarkup(rowsInline));

        return message;
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
        Message receiveMessage = update.getMessage();
        if (update.hasMessage() && receiveMessage.hasText()) {
            String text = receiveMessage.getText();
            Long chatId = receiveMessage.getChatId();
            Long tgUserId = receiveMessage.getFrom().getId();
            log.info("tgUserId: {}, text: {}", tgUserId, text);
            Optional<User> userOpt = userService.getByTgUserId(tgUserId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if (userOpt.isEmpty()) {
                sendMessage.setText("请先绑定账号");
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    log.error("TelegramApiException: {}", e.getMessage());
                }
                return;
            }

            User user = userOpt.get();
            log.info("user: {}", user);

            if (text.startsWith("/")) {
                String commandAnswer;
                switch (text) {
                    case "/today" -> {
                        BigDecimal amount = recordService.getAmountByDate(user.getId(), DateUtil.today());
                        commandAnswer = "今日支出: " + amount;
                    }
                    case "/yesterday" -> {
                        BigDecimal amount = recordService.getAmountByDate(user.getId(), DateUtil.yesterday().toDateStr());
                        commandAnswer = "昨日支出: " + amount;
                    }
                    case "/last_month" -> {
                        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyy-MM");
                        BigDecimal amount = recordService.getAmountByMonth(user.getId(), lastMonth);
                        commandAnswer = "上月支出: " + amount;
                    }
                    case "/current_month" -> {
                        String currentMonth = DateUtil.format(DateUtil.date(), "yyyy-MM");
                        BigDecimal amount = recordService.getAmountByMonth(user.getId(), currentMonth);
                        commandAnswer = "本月支出: " + amount;
                    }
                    case "/rule" -> {
                        SendMessage message = getRuleMessage(chatId, user);
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    default -> {
                        log.error("unknown command: {}", text);
                        return;
                    }
                }

                sendMessage.setText(commandAnswer);
            } else {
                // 快速记账
                String sendText = quickRecord(text, sendMessage, user);
                if (sendText == null) {
                    return;
                }

                sendMessage.setText(sendText);
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("TelegramApiException: {}", e.getMessage());
            }

        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            Long agUserId = update.getCallbackQuery().getFrom().getId();
            Message updateMessage = update.getCallbackQuery().getMessage();
            Integer messageId = updateMessage.getMessageId();
            long chatId = updateMessage.getChatId();

            Optional<User> userOpt = userService.getByTgUserId(agUserId);
            if (userOpt.isEmpty()) {
                return;
            }

            String answerText = "ok, ok";
            EditMessageText message = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(answerText)
                    .build();
            if (callData.contains("::")) {
                String[] split = callData.split("::");
                switch (split[0]) {
                    case "record" -> {
                        String recordId = split[1];
                        Record record = recordService.getById(recordId);
                        if (record == null) {
                            return;
                        }

                        Account account = accountService.getById(record.getAccountId());
                        if (Objects.equals(record.getTransactionType(), TransactionType.EXPENSE.getType())) {
                            account.setBalance(account.getBalance().add(record.getAmount()));
                            account.setConsume(account.getConsume().subtract(record.getAmount()));
                        } else {
                            account.setBalance(account.getBalance().subtract(record.getAmount()));
                            account.setIncome(account.getIncome().subtract(record.getAmount()));
                        }

                        account.updateById();
                        record.deleteById();

                        answerText = "删除成功";
                    }

                    case "rule" -> {
                        answerText = getRuleDetails(message, split);
                    }

                    case "rule_del" -> {
                        String ruleId = split[1];
                        Rule rule = ruleService.getById(ruleId);
                        if (rule == null) {
                            return;
                        }

                        rule.deleteById();
                        answerText = "删除成功";
                    }
                }
            }

            message.setText(answerText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    private String getRuleDetails(EditMessageText message, String[] split) {
        String answerText;
        String ruleId = split[1];
        Rule rule = ruleService.getById(ruleId);
        answerText = StrUtil.format("""
                        规则名称:   {}\r
                        关键词:     #{}\r
                        分类:         #{}\r
                        标签:         #{}\r
                        类型:         #{}\r
                        金额:         {}
                        """,
                rule.getName(),
                rule.getKeywords(),
                Optional.ofNullable(categoryService.getById(rule.getCategoryId())).map(Category::getName).orElse(""),
                Optional.ofNullable(tagService.getById(rule.getTagId())).map(Tag::getName).orElse(""),
                TransactionType.valueOf(rule.getTransactionType()).getDesc(),
                rule.getAmount()
        );
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("删除")
                .callbackData("rule_del::" + rule.getId())
                .build();

        buttons.add(button);
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(buttons);
        message.setReplyMarkup(new InlineKeyboardMarkup(rowsInline));
        return answerText;
    }

    @Transactional
    String quickRecord(String text, SendMessage sendMessage, User user) {
        Record record = Record.builder()
                .title(text)
                .userId(user.getId())
                .date(DateUtil.today())
                .accountId(user.getDefaultAccountId())
                .transactionType(TransactionType.EXPENSE.getType())
                .build();
        // 关键词查找规则
        Optional<Rule> ruleOpt = ruleService.findByKeyword(user.getId(), text);
        if (ruleOpt.isPresent()) {
            Rule rule = ruleOpt.get();
            log.info("rule: {}", rule);
            record
                    .setTagId(rule.getTagId())
                    .setAccountId(rule.getAccountId())
                    .setCategoryId(rule.getCategoryId())
                    .setTransactionType(rule.getTransactionType())
                    .setAmount(rule.getAmount())
                    .setRemark("关键词匹配");

        } else {
            // 提取消息中的金额
            String amount = MoneyUtil.getAmount(text);
            if (amount == null) {
                return null;
            }

            record.setAmount(new BigDecimal(amount)).setRemark("快速记账");
        }

        recordService.insert(record);

        BigDecimal amount = record.getAmount();
        Account account = accountService.getById(record.getAccountId());
        if (Objects.equals(record.getTransactionType(), TransactionType.EXPENSE.getType())) {
            account.setBalance(account.getBalance().subtract(amount));
            account.setConsume(account.getConsume().add(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
            account.setIncome(account.getIncome().add(amount));
        }

        account.updateById();

        String callbackData = "record::" + record.getId();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        InlineKeyboardButton delButton = InlineKeyboardButton.builder()
                .text("删除")
                .callbackData(callbackData)
                .build();

        buttons.add(delButton);
        rowsInline.add(buttons);

        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(rowsInline));

        return StrUtil.format("记账成功 \r\n账户:        #{}\r\n余额:        {} \r\n金额:        {}",
                account.getAccountName(),
                account.getBalance(),
                amount
        );
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
        commands.add(new BotCommand("rule", "规则"));

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
