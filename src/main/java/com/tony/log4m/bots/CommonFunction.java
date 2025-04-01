package com.tony.log4m.bots;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void mainFunc(TelegramBot bot, Update update) {
        if (update.message() != null) {
            handleTextMessage(bot, update.message());
        } else if (update.callbackQuery() != null) {
            handleCallbackQuery(bot, update.callbackQuery());
        } else {
            log.error("unknown update: {}", update);
        }
    }

    private void handleTextMessage(TelegramBot bot, Message message) {
        String text = message.text();
        Long chatId = message.chat().id();
        Long tgUserId = message.from().id();
        log.info("tgUserId: {}, text: {}", tgUserId, text);

        SendMessage responseMessage;
        if (text.startsWith("/")) {
            responseMessage = processCommand(text, chatId);
        } else {
            responseMessage = processQuickRecord(text, chatId);
        }
        if (responseMessage != null) {
            bot.execute(responseMessage);
        }
    }

    /**
     * 处理命令消息
     */
    private SendMessage processCommand(String text, Long chatId) {
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
                return getRuleMessage(chatId);
            }
            default -> {
                log.error("unknown command: {}", text);
                return null;
            }
        }
        return new SendMessage(chatId, commandAnswer);
    }

    /**
     * 处理快速记账消息
     */
    private SendMessage processQuickRecord(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        String resultText = quickRecord(text, sendMessage);
        if (resultText == null) {
            return null;
        }
        return new SendMessage(chatId, resultText);
    }

    /**
     * 处理回调查询
     */
    private void handleCallbackQuery(TelegramBot bot, com.pengrad.telegrambot.model.CallbackQuery callbackQuery) {
        String callData = callbackQuery.data();
        MaybeInaccessibleMessage updateMessage = callbackQuery.maybeInaccessibleMessage();
        long chatId = updateMessage.chat().id();
        Integer messageId = updateMessage.messageId();

        String answerText = "ok, ok";
        if (callData.contains("::")) {
            String[] split = callData.split("::");
            switch (split[0]) {
                case "record" -> answerText = handleRecordDeletion(split[1]);
                case "rule" -> answerText = handleRuleDetails(split[1]);
                case "rule_del" -> answerText = handleRuleDeletion(split[1]);
                default -> log.error("unknown callback command: {}", split[0]);
            }
        }
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, answerText);
        bot.execute(editMessageText);
    }

    /**
     * 删除记账记录的回调处理
     */
    private String handleRecordDeletion(String recordId) {
        Bill bill = billService.getById(recordId);
        if (bill == null) {
            return "记录不存在";
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
        return "删除成功";
    }

    /**
     * 返回规则详情（包括更新 inline 按钮）
     */
    private String handleRuleDetails(String ruleId) {
        Rule rule = ruleService.getById(ruleId);
        if (rule == null) {
            return "规则不存在";
        }
        String answerText = StrUtil.format("""
                        规则名称:   {}\r
                        关键词:     #{}\r
                        分类:         #{}\r
                        标签:         #{}\r
                        类型:         #{}\r
                        金额:         {}
                        """,
                rule.getName(),
                rule.getKeywords(),
                Optional.ofNullable(categoryService.getById(rule.getCategoryId()))
                        .map(Category::getName).orElse(""),
                Optional.ofNullable(tagService.getById(rule.getTagId()))
                        .map(Tag::getName).orElse(""),
                rule.getTransactionType().getDesc(),
                rule.getAmount()
        );

        // 如果需要更新按钮，可额外构造 InlineKeyboardMarkup 后通过 EditMessageText.replyMarkup 传递
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton delButton = new InlineKeyboardButton();
        delButton.setText("删除");
        delButton.callbackData("rule_del::" + rule.getId());
        inlineKeyboardMarkup.addRow(delButton);
        // 此处示例中没有直接使用 inlineKeyboardMarkup，但可按需求修改
        return answerText;
    }

    /**
     * 删除规则的回调处理
     */
    private String handleRuleDeletion(String ruleId) {
        Rule rule = ruleService.getById(ruleId);
        if (rule == null) {
            return "规则不存在";
        }
        rule.deleteById();
        return "删除成功";
    }

    /**
     * 获取规则列表消息（带 inline 按钮）
     */
    private SendMessage getRuleMessage(Long chatId) {
        SendMessage message = new SendMessage(chatId, "规则列表");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ruleService.query().list().forEach(rule -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(rule.getName());
            button.setCallbackData("rule::" + rule.getId());
            inlineKeyboardMarkup.addRow(button);
        });
        message.replyMarkup(inlineKeyboardMarkup);
        return message;
    }

    /**
     * 快速记账处理，支持规则匹配和金额提取
     */
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
            bill.setTagId(rule.getTagId())
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
            bill.setAccountId(1)
                    .setAmount(new BigDecimal(amount))
                    .setRemark("快速记账");
        }
        bill.setBillDay(LocalDate.now());
        bill.setBillMonth(LocalDate.now().getMonthValue());
        bill.insert();

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

        // 增加删除按钮的回调数据
        String callbackData = "record::" + bill.getId();
        InlineKeyboardButton delButton = new InlineKeyboardButton();
        delButton.setText("删除");
        delButton.callbackData(callbackData);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(delButton));

        return StrUtil.format("记账成功 \r\n账户:        #{}\r\n余额:        {} \r\n金额:        {}",
                account.getAccountName(),
                account.getBalance(),
                amount);
    }
}
