package com.tony.log4m.bots;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.commands.CommandStrategy;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.convert.CategoryConvert;
import com.tony.log4m.convert.RuleConvert;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.utils.CommonUtil;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tony
 * @since 4/10/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private final Map<String, CommandStrategy> systemStrategies;

    private final RuleService ruleService;
    private final AccountService accountService;
    private final CategoryService categoryService;


    public SendMessage handleCommand(String text, Long chatId) {
        // 使用 split 分割，最多分割3次（保证参数部分保留）
        String[] parts = text.split("/", 3);

        // 提取命令和参数
        String command = parts.length > 1 ? parts[1] : "";
        String param = parts.length > 2 ? parts[2] : "";

        Command menuCmd = Command.getByCommand(command);
        String strategy = menuCmd.getStrategy() + "Command";
        return systemStrategies.get(strategy).execute(menuCmd, param, chatId);
    }


    /**
     * 快速记账处理，支持规则匹配和金额提取
     */
    public SendMessage handleQuickRecord(String text, Long chatId) {
        Bill bill = saveBill(text);

        BigDecimal amount = bill.getAmount();
        String amountPrefix = bill.getTransactionType().getPrefix();

        // 返回模板
        String template = """
                记账成功
                ---------
                金额:        {}
                日期:        {}
                备注:        {}
                分类:        {}
                """;

        String replyText = StrUtil.format(
                template,
                amountPrefix + amount,
                bill.getBillDate(),
                bill.getNote(),
                bill.getCategoryName()
        );

        SendMessage sendMessage = new SendMessage(chatId, replyText);

        // 增加删除按钮的回调数据
        String callbackData = "record::" + bill.getBillId();
        InlineKeyboardButton delButton = new InlineKeyboardButton();
        delButton.setText("删除");
        delButton.callbackData(callbackData);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(delButton));

        return sendMessage;
    }

    private Bill saveBill(String text) {
        LocalDateTime now = LocalDateTime.now();
        Bill bill = Bill.builder()
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
                bill.setAccountId(account.getAccountId());
            }
            bill.setNote(rule.getRuleName());
        } else {
            // 提取时间
            MoneyUtil.Result result = MoneyUtil.getDate(text);
            LocalDate billDate = result.date();
            text = result.text();
            bill
                    .setBillDate(billDate)
                    .setBillMonth(LocalDateTimeUtil.format(now, "yyyyMM"));

            // 提取消息中的金额
            String amount = MoneyUtil.getAmount(text);
            bill.setAmount(new BigDecimal(amount));

            // 获取除amount外的text
            String note = text.replace(amount, "");
            bill.setNote(note);

            // 是否匹配分类
            if (StrUtil.isNotBlank(note)) {
                categoryService.lambdaQuery().eq(Category::getCategoryName, note)
                        .oneOpt()
                        .ifPresent(category -> {
                            CategoryConvert.INSTANCE.updateBill(bill, category);
                            bill.setNote("");
                        });
            }

            // 获取默认账户
            Account account = accountService.getOrCreateDefaultAccount();
            bill.setAccountId(account.getAccountId());
        }

        bill.insert();
        return bill;
    }


}