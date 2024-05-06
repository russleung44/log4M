package com.tony.log4m.bots;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author TonyLeung
 * @since 2024/5/5
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonFunction {

    private final TagService tagService;
    private final RuleService ruleService;
    private final BillService billService;
    private final AccountService accountService;
    private final CategoryService categoryService;


    public void mainFunc(TelegramClient telegramClient, Update update) {
        Message receiveMessage = update.getMessage();
        if (update.hasMessage() && receiveMessage.hasText()) {
            String text = receiveMessage.getText();
            Long chatId = receiveMessage.getChatId();
            Long tgUserId = receiveMessage.getFrom().getId();
            log.info("tgUserId: {}, text: {}", tgUserId, text);
            SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("收到消息:" + text).build();
            if (text.startsWith("/")) {
                String commandAnswer;
                switch (text) {
                    case "/today" -> {
                        BigDecimal amount = billService.getAmountByDate(DateUtil.today());
                        commandAnswer = "今日支出: " + amount;
                    }
                    case "/yesterday" -> {
                        BigDecimal amount = billService.getAmountByDate(DateUtil.yesterday().toDateStr());
                        commandAnswer = "昨日支出: " + amount;
                    }
                    case "/last_month" -> {
                        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyy-MM");
                        BigDecimal amount = billService.getAmountByMonth(lastMonth);
                        commandAnswer = "上月支出: " + amount;
                    }
                    case "/current_month" -> {
                        String currentMonth = DateUtil.format(DateUtil.date(), "yyyy-MM");
                        BigDecimal amount = billService.getAmountByMonth(currentMonth);
                        commandAnswer = "本月支出: " + amount;
                    }
                    case "/rule" -> {
                        SendMessage message = getRuleMessage(chatId);
                        try {
                            telegramClient.execute(message);
                        } catch (TelegramApiException e) {
                            log.error("规则发送失败", e);
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
                String sendText = quickRecord(text, sendMessage);
                if (sendText == null) {
                    return;
                }

                sendMessage.setText(sendText);
            }
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("TelegramApiException: {}", e.getMessage());
            }

        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            MaybeInaccessibleMessage updateMessage = update.getCallbackQuery().getMessage();
            long chatId = updateMessage.getChatId();
            Integer messageId = updateMessage.getMessageId();


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
                        Bill bill = billService.getById(recordId);
                        if (bill == null) {
                            return;
                        }

                        Account account = accountService.getById(bill.getAccountId());
                        if (Objects.equals(bill.getTransactionType(), TransactionType.EXPENSE)) {
                            account.setBalance(account.getBalance().add(bill.getAmount()));
                            account.setConsume(account.getConsume().subtract(bill.getAmount()));
                        } else {
                            account.setBalance(account.getBalance().subtract(bill.getAmount()));
                            account.setIncome(account.getIncome().subtract(bill.getAmount()));
                        }

                        account.updateById();
                        bill.deleteById();

                        answerText = "删除成功";
                    }

                    case "rule" -> answerText = getRuleDetails(message, split);

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
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error("Telegram 请求失败:{}", e.getMessage());
            }
        }

    }


    private SendMessage getRuleMessage(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("规则列表")
                .build();

        List<InlineKeyboardRow> buttons = new ArrayList<>();
        ruleService.query().list().forEach(rule -> {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(rule.getName())
                    .callbackData("rule::" + rule.getId())
                    .build();

            buttons.add(new InlineKeyboardRow(button));
        });
        message.setReplyMarkup(new InlineKeyboardMarkup(buttons));

        return message;
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
                rule.getTransactionType().getDesc(),
                rule.getAmount()
        );
        List<InlineKeyboardRow> buttons = new ArrayList<>();
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("删除")
                .callbackData("rule_del::" + rule.getId())
                .build();

        buttons.add(new InlineKeyboardRow(button));
        message.setReplyMarkup(new InlineKeyboardMarkup(buttons));
        return answerText;
    }

    public String quickRecord(String text, SendMessage sendMessage) {
        Bill bill = Bill.builder()
                .title(text)
                .billDate(LocalDateTime.now())
                .transactionType(TransactionType.EXPENSE)
                .build();
        // 关键词查找规则
        Optional<Rule> ruleOpt = ruleService.findByKeyword(text);
        if (ruleOpt.isPresent()) {
            Rule rule = ruleOpt.get();
            log.info("rule: {}", rule);
            bill
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

            bill.setAmount(new BigDecimal(amount)).setRemark("快速记账");
        }

        billService.save(bill);

        BigDecimal amount = bill.getAmount();
        Account account = accountService.getById(bill.getAccountId());
        if (Objects.equals(bill.getTransactionType(), TransactionType.EXPENSE)) {
            account.setBalance(account.getBalance().subtract(amount));
            account.setConsume(account.getConsume().add(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
            account.setIncome(account.getIncome().add(amount));
        }

        account.updateById();

        String callbackData = "record::" + bill.getId();
        List<InlineKeyboardRow> buttons = new ArrayList<>();
        InlineKeyboardButton delButton = InlineKeyboardButton.builder()
                .text("删除")
                .callbackData(callbackData)
                .build();

        buttons.add(new InlineKeyboardRow(delButton));

        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(buttons));

        return StrUtil.format("记账成功 \r\n账户:        #{}\r\n余额:        {} \r\n金额:        {}",
                account.getAccountName(),
                account.getBalance(),
                amount
        );
    }


}
