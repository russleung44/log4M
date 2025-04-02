package com.tony.log4m.bots;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.convert.RuleConvert;
import com.tony.log4m.enums.CommandType;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    private final UserService userService;

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

        User user = userService.getByTgUserId(tgUserId);

        log.info("tgUserId: {}, text: {}", tgUserId, text);
        if (StrUtil.isBlank(text)) {
            return;
        }

        SendMessage responseMessage;
        if (text.startsWith("/")) {
            responseMessage = processCommand(text, chatId);
        } else {
            responseMessage = processQuickRecord(text, chatId, user);
        }
        if (responseMessage != null) {
            bot.execute(responseMessage);
        }
    }

    /**
     * 处理命令消息
     */
    private SendMessage processCommand(String text, Long chatId) {
        CommandType commandType = CommandType.getByCode(text.replace("/", ""));
        String desc = commandType.getDesc();

        BigDecimal amount = BigDecimal.ZERO;
        switch (commandType) {
            case TODAY -> {
                amount = billService.getAmountByDate(DateUtil.today());
            }
            case YESTERDAY -> {
                amount = billService.getAmountByDate(DateUtil.yesterday().toDateStr());
            }
            case LAST_MONTH -> {
                String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyy-MM");
                amount = billService.getAmountByMonth(lastMonth);
            }
            case THIS_MONTH -> {
                String currentMonth = DateUtil.format(DateUtil.date(), "yyyy-MM");
                amount = billService.getAmountByMonth(currentMonth);
            }
            case RULES -> {
                return getRuleMessage(chatId);
            }
        }

        String commandAnswer = desc + ": " + amount;
        return new SendMessage(chatId, commandAnswer);
    }

    /**
     * 快速记账处理，支持规则匹配和金额提取
     */
    private SendMessage processQuickRecord(String text, Long chatId, User user) {
        Bill bill = saveBill(text, user);
        if (bill == null) return null;

        BigDecimal amount = bill.getAmount();
        Account account = updateAccount(bill, amount);

        // 返回模板
        String template = """
                记账成功\s
                账户:        #{}
                余额:        {}\s
                金额:        {}""";

        String replyText = StrUtil.format(
                template,
                account.getAccountName(),
                account.getBalance(),
                amount);

        SendMessage sendMessage = new SendMessage(chatId, replyText);

        // 增加删除按钮的回调数据
        String callbackData = "record::" + bill.getId();
        InlineKeyboardButton delButton = new InlineKeyboardButton();
        delButton.setText("删除");
        delButton.callbackData(callbackData);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(delButton));

        return sendMessage;
    }

    @NotNull
    private Account updateAccount(Bill bill, BigDecimal amount) {
        Account account = accountService.getById(bill.getAccountId());
        if (Objects.equals(bill.getTransactionType(), TransactionType.EXPENSE)) {
            account.setBalance(account.getBalance().subtract(amount));
            account.setConsume(account.getConsume().add(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
            account.setIncome(account.getIncome().add(amount));
        }
        account.updateById();
        return account;
    }

    @Nullable
    private Bill saveBill(String text, User user) {
        Long userId = user.getId();
        Bill bill = Bill.builder()
                .title(text)
                .userId(userId)
                .billDay(LocalDate.now())
                .billDate(LocalDateTime.now())
                .billMonth(LocalDate.now().getMonthValue())
                .transactionType(TransactionType.EXPENSE)
                .build();
        // 关键词查找规则
        Optional<Rule> ruleOpt = ruleService.findByKeyword(text);
        if (ruleOpt.isPresent()) {
            Rule rule = ruleOpt.get();
            log.info("rule: {}", rule);

            RuleConvert.INSTANCE.updateBill(rule, bill);
            bill.setRemark("关键词匹配");
        } else {
            // 提取消息中的金额
            String amount = MoneyUtil.getAmount(text);
            if (amount == null) {
                return null;
            }
            bill
                    .setAmount(new BigDecimal(amount))
                    .setRemark("快速记账");

            // 获取默认账户
            Account account = accountService.getDefaultAccount(userId);
            bill.setAccountId(account.getId());
        }

        bill.insert();
        return bill;
    }

    /**
     * 处理回调查询
     */
    private void handleCallbackQuery(TelegramBot bot, CallbackQuery callbackQuery) {
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


}
