package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.models.entity.Rule;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 规则命令
 *
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class RuleCommand implements CommandStrategy {

    private final RuleService ruleService;
    private final CategoryService categoryService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        switch (command) {
            case RULES -> {
                return getRuleMessage(chatId);
            }
            case RULE_ADD -> {
                return addRule(param.split("-"), chatId);
            }
            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }

    /**
     * 添加规则
     * 关键字-金额-交易类型-分类
     */
    public SendMessage addRule(String[] params, Long chatId) {
        if (params.length < 3 || params.length > 4) {
            return new SendMessage(chatId, "参数错误");
        }

        // 检查参数是否为空
        for (String param : params) {
            if (param == null || param.trim().isEmpty()) {
                return new SendMessage(chatId, "参数包含空值");
            }
        }

        String keyword = params[0];
        String amountStr = params[1];
        String transactionTypeStr = params[2];

        // 验证交易类型
        if (!"0".equals(transactionTypeStr) && !"1".equals(transactionTypeStr)) {
            return new SendMessage(chatId, "交易类型参数无效");
        }

        // 解析金额并处理异常
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return new SendMessage(chatId, "金额参数无效");
        }

        TransactionType transactionType = "1".equals(transactionTypeStr)
                ? TransactionType.EXPENSE
                : TransactionType.INCOME;

        Rule rule = new Rule(keyword, amount, transactionType);

        // 处理分类
        if (params.length == 4) {
            String categoryName = params[3];
            Category category = categoryService.getOrCreate(categoryName);
            rule.setCategoryId(category.getCategoryId());
        }

        rule.insert();

        String ruleDetails = ruleService.buildRuleDetails(rule);
        InlineKeyboardMarkup inlineKeyboardMarkup = BotUtil.buildKeyboardMarkup("rule::" + rule.getRuleId());
        SendMessage sendMessage = new SendMessage(chatId, ruleDetails);
        sendMessage.replyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    /**
     * 获取规则列表消息（带 inline 按钮）
     */
    private SendMessage getRuleMessage(Long chatId) {
        // 查询最多100条规则，按排序和创建时间倒序
        List<Rule> rules = ruleService.lambdaQuery()
                .orderByAsc(Rule::getSort)
                .orderByDesc(Rule::getCrTime)
                .last("LIMIT 100")
                .list();

        StringBuilder text = new StringBuilder();
        if (rules.isEmpty()) {
            text.append("暂无规则\n")
                    .append("使用如下命令快速添加:\n")
                    .append("/rule_add/{规则名称}-{金额}-{1:支付,0:收入}-{分类}");
        } else {
            text.append("📐 规则列表（共").append(rules.size()).append("条，最多显示100条）\n")
                    .append("点击下方按钮查看详情或删除\n")
                    .append("——————————————\n");
            int i = 1;
            int displayCount = Math.min(rules.size(), 30);
            for (int idx = 0; idx < displayCount; idx++) {
                Rule rule = rules.get(idx);
                String typeIcon = rule.getTransactionType() == TransactionType.EXPENSE ? "🔻支出" : "🔺收入";
                String categoryName = categoryService.getCategoryName(rule.getCategoryId());
                String amountStr = com.tony.log4m.utils.MoneyUtil.formatBigDecimal(rule.getAmount());
                text.append(i++).append(". ")
                        .append(rule.getRuleName()).append("  ¥").append(amountStr)
                        .append("  ").append(typeIcon)
                        .append("  #").append(categoryName)
                        .append("\n");
            }
            if (rules.size() > displayCount) {
                text.append("…… 其余").append(rules.size() - displayCount).append("条请点击下方按钮查看\n");
            }
        }

        SendMessage message = new SendMessage(chatId, text.toString());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        rules.forEach(rule -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(rule.getRuleName());
            button.setCallbackData("rule::" + rule.getRuleId());
            inlineKeyboardMarkup.addRow(button);
        });
        message.replyMarkup(inlineKeyboardMarkup);
        return message;
    }
}
