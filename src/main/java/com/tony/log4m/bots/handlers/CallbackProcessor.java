package com.tony.log4m.bots.handlers;

import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.core.BotUtil;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static com.tony.log4m.enums.TransactionType.EXPENSE;

/**
 * @author Tony
 * @since 4/10/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackProcessor {

    private final BillService billService;
    private final TagService tagService;
    private final RuleService ruleService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    /**
     * 处理所有回调查询
     */
    public void handle(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        Long chatId = message.chat().id();
        Integer messageId = message.messageId();

        try {
            String responseText = processCallbackData(data);
            InlineKeyboardMarkup markup = buildKeyboardMarkup(data);

            EditMessageText editRequest = new EditMessageText(chatId, messageId, responseText)
                    .replyMarkup(markup);
            bot.execute(editRequest);

        } catch (NoSuchElementException e) {
            log.warn("数据不存在: {}", e.getMessage());
            bot.execute(new SendMessage(chatId, "⚠️ 数据不存在或已被删除"));
        } catch (Exception e) {
            log.error("回调处理失败: {}", e.getMessage(), e);
            bot.execute(new SendMessage(chatId, "❌ 系统错误，请稍后再试"));
        }
    }


    private String buildBillDetails(String targetId) {
        Bill bill = billService.getOptById(targetId).orElseThrow();
        return BotUtil.getBillFormatted(bill);
    }


    private String buildCategoryDetails(String categoryId) {
        Category category = categoryService.getOptById(categoryId).orElseThrow();

        String template = """
                分类详情
                名称: {}
                父类: {}
                """;

        return StrUtil.format(
                template,
                category.getCategoryName(),
                getCategoryName(category.getParentCategoryId())
        );
    }

    private String buildRuleDetails(String ruleId) {
        Rule rule = ruleService.getOptById(ruleId).orElseThrow();

        return StrUtil.format("""
                        规则详情
                        名称: {}
                        关键词: {}
                        分类: {}
                        标签: {}
                        类型: {}
                        金额: {}
                        """,
                rule.getRuleName(),
                rule.getKeywords(),
                getCategoryName(rule.getCategoryId()),
                getTagName(rule.getTagId()),
                rule.getTransactionType().getType(),
                rule.getAmount());
    }


    private void reverseAccountChanges(Account account, Bill bill) {
        BigDecimal amount = bill.getAmount();
        if (bill.getTransactionType() == EXPENSE) {
            account.setBalance(account.getBalance().add(amount))
                    .setConsume(account.getConsume().subtract(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount))
                    .setIncome(account.getIncome().subtract(amount));
        }
        accountService.update(account);
    }

    private String deleteRule(String ruleId) {
        Rule rule = ruleService.getOptById(ruleId).orElseThrow();
        rule.deleteById();
        return "✅ 规则已删除";
    }

    private String deleteCategory(String categoryId) {
        Category category = categoryService.getOptById(categoryId).orElseThrow();
        category.deleteById();
        return "✅ 分类已删除";
    }

    private String deleteBill(String recordId) {
        Bill bill = billService.getOptById(recordId).orElseThrow();

        Account account = accountService.getOptById(bill.getAccountId()).orElseThrow();
        reverseAccountChanges(account, bill);
        bill.deleteById();

        return "✅ 记录删除成功\n"
                + "账户: " + account.getAccountName() + "\n"
                + "余额: " + account.getBalance();
    }


    private String processCallbackData(String data) {
        String[] parts = data.split("::");
        String prefix = parts[0];
        String targetId = parts[1];

        return switch (prefix) {
            case "bill" -> buildBillDetails(targetId);
            case "rule" -> buildRuleDetails(targetId);
            case "category" -> buildCategoryDetails(targetId);
            case "bill_del" -> deleteBill(targetId);
            case "rule_del" -> deleteRule(targetId);
            case "category_del" -> deleteCategory(targetId);
            default -> throw new IllegalArgumentException("未知操作类型: " + prefix);
        };
    }

    private InlineKeyboardMarkup buildKeyboardMarkup(String data) {
        // 输入校验
        if (data == null || !data.contains("::")) {
            throw new IllegalArgumentException("Invalid input format: expected 'prefix::targetId'");
        }

        String[] parts = data.split("::", 2); // 确保只分割两次，避免过多部分
        String prefix = parts[0];
        String targetId = parts.length > 1 ? parts[1] : "";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // 使用 switch 表达式（Java 12+）简化逻辑
        InlineKeyboardButton button = switch (prefix) {
            case "rule" -> createButton("❌ 删除规则", "rule_del", targetId);
            case "category" -> createButton("❌ 删除分类", "category_del", targetId);
            case "bill" -> createButton("❌ 删除记录", "bill_del", targetId);
            default -> {
                System.out.println("Unknown prefix: " + prefix);
                yield null; // 返回 null 表示未知前缀
            }
        };

        if (button != null) {
            inlineKeyboardMarkup.addRow(button);
        }

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createButton(String text, String callbackPrefix, String targetId) {
        return new InlineKeyboardButton(text).callbackData(callbackPrefix + "::" + targetId);
    }


    private String getCategoryName(Long categoryId) {
        return categoryService.getOptById(categoryId)
                .map(Category::getCategoryName)
                .orElse("未分类");
    }

    private String getTagName(Long tagId) {
        return tagService.getOptById(tagId)
                .map(Tag::getTagName)
                .orElse("无标签");
    }
}