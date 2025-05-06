package com.tony.log4m.bots;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.commands.CommandStrategy;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.convert.CategoryConvert;
import com.tony.log4m.convert.RuleConvert;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.pojo.entity.Rule;
import com.tony.log4m.service.AccountService;
import com.tony.log4m.service.BillService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.service.RuleService;
import com.tony.log4m.utils.CommonUtil;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandHandler {

    private static final String DELETE_CALLBACK = "bill_del";
    private static final String HASH_TAG = "#";

    private final Map<String, CommandStrategy> systemStrategies;
    private final RuleService ruleService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final BillService billService;

    public SendMessage handleCommand(String text, Long chatId) {
        if (StrUtil.isBlank(text)) {
            return new SendMessage(chatId, "请输入有效命令");
        }
        String[] parts = text.trim().split("/", 3);
        Command cmd = Command.getByCommand(parts.length > 1 ? parts[1] : "");
        String key = cmd.getStrategy() + "Command";

        CommandStrategy strategy = systemStrategies.get(key);
        if (strategy == null) {
            return new SendMessage(chatId, "未识别的命令：" + cmd.getCommand());
        }
        return strategy.execute(cmd, parts.length > 2 ? parts[2] : "", chatId);
    }

    @Transactional
    public SendMessage handleQuickRecord(String text, Long chatId) {
        Bill bill = saveBill(text);
        String reply = BotUtil.getBillFormatted(bill);
        return new SendMessage(chatId, reply).replyMarkup(createDeleteMarkup(bill.getBillId()));
    }

    private InlineKeyboardMarkup createDeleteMarkup(Long billId) {
        return BotUtil.createButton("删除", DELETE_CALLBACK, billId);
    }

    protected Bill saveBill(String rawText) {
        String text = StrUtil.trim(rawText);
        Bill bill = createDefaultBill();

        Account acct = accountService.getOrCreateDefaultAccount();
        bill.setAccountId(acct.getAccountId());

        // 1. 规则匹配优先
        Optional<Rule> ruleOpt = ruleService.findByKeyword(text);
        if (ruleOpt.isPresent()) {
            applyRule(bill, ruleOpt.get());
        } else {
            // 2. 自由文本解析
            parseFreeText(bill, text);
        }

        // 3. 公共字段
        bill.setBillMonth(MoneyUtil.getMonth(bill.getBillDate()));
        // 4. 持久化
        bill.insert();
        return bill;
    }

    private Bill createDefaultBill() {
        return Bill.builder()
                .billDate(LocalDate.now())
                .transactionType(TransactionType.EXPENSE)
                .build();
    }

    private void applyRule(Bill bill, Rule rule) {
        log.debug("应用规则：{}", rule.getRuleName());
        RuleConvert.INSTANCE.updateBill(bill, rule);
        if (CommonUtil.isZero(bill.getAccountId())) {
            bill.setAccountId(accountService.getOrCreateDefaultAccount().getAccountId());
        }
        // 查询分类
        Long categoryId = rule.getCategoryId();
        if (CommonUtil.isNotZero(categoryId)) {
            categoryService.getOptById(categoryId).ifPresent(c -> {
                CategoryConvert.INSTANCE.updateBill(bill, c);
            });
        }

        bill.setNote(StrUtil.nullToEmpty(rule.getRuleName()));
    }

    private void parseFreeText(Bill bill, String text) {
        // 1. 提取日期
        MoneyUtil.Result dateRes = MoneyUtil.getDate(text);
        bill.setBillDate(dateRes.date());
        text = dateRes.text().trim();

        // 2. 提取金额
        String amt = MoneyUtil.getAmount(text);
        bill.setAmount(new BigDecimal(amt));
        text = StrUtil.trim(text.replace(amt, ""));

        if (StrUtil.isNotBlank(text)) {
            // 3. 分类与备注
            if (text.contains(HASH_TAG)) {
                String catName = StrUtil.subAfter(text, HASH_TAG, false).trim();
                Category category = categoryService.getOrCreate(catName);
                CategoryConvert.INSTANCE.updateBill(bill, category);
                // 获取分类以外的备注
                bill.setNote(StrUtil.subBefore(text, HASH_TAG, false).trim());
            } else {
                bill.setNote(text);
            }
        }
    }


}
