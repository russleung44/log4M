package com.tony.log4m.bots;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.commands.custom.CustomCommandStrategy;
import com.tony.log4m.bots.commands.system.SystemCommandStrategy;
import com.tony.log4m.bots.enums.CustomCommand;
import com.tony.log4m.bots.enums.MenuCommand;
import com.tony.log4m.convert.RuleConvert;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.utils.CommonUtil;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Tony
 * @since 4/10/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private final Map<String, SystemCommandStrategy> systemStrategies;
    private final Map<String, CustomCommandStrategy> customStrategies;

    private final RuleService ruleService;
    private final AccountService accountService;


    public SendMessage handleSystemCommand(String command, Long chatId) {
        MenuCommand menuCmd = MenuCommand.getByCommand(command);
        return systemStrategies.get(menuCmd.getStrategy()).execute(menuCmd, chatId);
    }

    public SendMessage handleCustomCommand(String text, Long chatId) {
        CommandParser parser = new CommandParser(text);
        CustomCommand customCommand = parser.getCommand();
        return customStrategies.get(customCommand.getStrategy()).execute(parser.getParams(), chatId);
    }


    /**
     * 快速记账处理，支持规则匹配和金额提取
     */
    public SendMessage handleQuickRecord(String text, Long chatId) {
        Bill bill = saveBill(text);
        if (bill == null) return null;

        BigDecimal amount = bill.getAmount();
        String amountPrefix = bill.getTransactionType() == TransactionType.EXPENSE ? "-" : "+";
        Account account = updateAccount(bill, amount);

        // 返回模板
        String template = """
                记账成功
                账户:        #{}
                余额:        {}
                金额:        {}""";

        String replyText = StrUtil.format(
                template,
                account.getName(),
                account.getBalance(),
                amountPrefix + amount);

        SendMessage sendMessage = new SendMessage(chatId, replyText);

        // 增加删除按钮的回调数据
        String callbackData = "record::" + bill.getId();
        InlineKeyboardButton delButton = new InlineKeyboardButton();
        delButton.setText("删除");
        delButton.callbackData(callbackData);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(delButton));

        return sendMessage;
    }

    private Bill saveBill(String text) {
        LocalDateTime now = LocalDateTime.now();
        Bill bill = Bill.builder()
                .title(text)
                .billDate(now)
                .billDay(now.toLocalDate())
                .billMonth(LocalDateTimeUtil.format(now, "yyyyMM"))
                .transactionType(TransactionType.EXPENSE)
                .build();

        // 关键词查找规则
        Optional<Rule> ruleOpt = ruleService.findByKeyword(text);
        if (ruleOpt.isPresent()) {
            Rule rule = ruleOpt.get();
            log.info("rule: {}", rule);
            RuleConvert.INSTANCE.updateBill(bill, rule);
            if (CommonUtil.isZero(bill.getAccountId())) {
                // 获取默认账户
                Account account = accountService.getOrCreateDefaultAccount();
                bill.setAccountId(account.getId());
            }
            bill.setNote("关键词匹配");
        } else {
            // 提取消息中的金额
            String amount = MoneyUtil.getAmount(text);
            if (amount == null) {
                return null;
            }

            // 获取除amount外的text
            bill
                    .setAmount(new BigDecimal(amount))
                    .setNote(text.replace(amount, ""));

            // 获取默认账户
            Account account = accountService.getOrCreateDefaultAccount();
            bill.setAccountId(account.getId());
        }

        bill.insert();
        return bill;
    }

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
}