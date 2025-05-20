package com.tony.log4m.bots.commands;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.service.BillService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillCommand implements CommandStrategy {

    private final BillService billService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        List<Bill> bills = fetchBillsByCommand(command);

        // 生成消息模板
        String template = generateTemplate(command, bills);

        // 生成键盘按钮
        InlineKeyboardMarkup inlineKeyboardMarkup = createKeyboardMarkup(command, bills);

        // 构建发送消息对象
        SendMessage sendMessage = new SendMessage(chatId, template);
        sendMessage.replyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    /**
     * 根据命令查询账单列表
     */
    private List<Bill> fetchBillsByCommand(Command command) {
        try {
            return switch (command) {
                case TODAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.today())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case YESTERDAY -> billService.lambdaQuery()
                        .eq(Bill::getBillDate, DateUtil.yesterday().toDateStr())
                        .orderByDesc(Bill::getBillDate)
                        .orderByDesc(Bill::getBillId)
                        .list();
                case LAST_MONTH, LAST_MONTH_SUMMARY -> {
                    String lastMonth = MoneyUtil.getMonth(DateUtil.lastMonth().toLocalDateTime().toLocalDate());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, lastMonth)
                            .orderByDesc(Bill::getBillDate)
                            .orderByDesc(Bill::getBillId)
                            .list();
                }
                case THIS_MONTH, THIS_MONTH_SUMMARY -> {
                    String currentMonth = MoneyUtil.getMonth(LocalDate.now());
                    yield billService.lambdaQuery()
                            .eq(Bill::getBillMonth, currentMonth)
                            .orderByDesc(Bill::getBillDate)
                            .orderByDesc(Bill::getBillId)
                            .list();
                }
                default -> new ArrayList<>();
            };
        } catch (Exception e) {
            log.error("Error fetching bills: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 计算账单总金额
     */
    private BigDecimal calculateTotalAmount(List<Bill> bills) {
        if (CollUtil.isEmpty(bills)) {
            return BigDecimal.ZERO;
        }

        return bills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 生成消息模板
     */
    private String generateTemplate(Command command, List<Bill> bills) {
        // 计算总金额
        BigDecimal amount = calculateTotalAmount(bills);
        String description = command.getDesc();
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            description = "无账单记录";
        }

        StringBuilder template = new StringBuilder();
        template.append(String.format("%s总计：%.2f元%n---------\n", description, amount));

        switch (command) {
            case LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY -> {
                // 按账单分类统计金额
                Map<String, Double> categoryMap = bills.stream()
                        .collect(Collectors.groupingBy(
                                Bill::getCategoryName,
                                Collectors.summingDouble(b -> b.getAmount().doubleValue())
                        ));

                // 如果分类为空，则替换为“未分类”
                if (categoryMap.containsKey(null) || categoryMap.containsKey("")) {
                    double unclassifiedAmount = categoryMap.getOrDefault(null, 0.0) +
                            categoryMap.getOrDefault("", 0.0);
                    categoryMap.put("未分类", unclassifiedAmount);
                    categoryMap.remove(null);
                    categoryMap.remove("");
                }

                // 找出最长分类名的长度，用于对齐
                int maxCategoryLength = categoryMap.keySet().stream()
                        .mapToInt(String::length)
                        .max()
                        .orElse(10);

                // 最小宽度是10
                int categoryLength = Math.max(maxCategoryLength, 10);

                // 按金额排序后添加到模板中
                categoryMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(entry -> {
                            String categoryName = entry.getKey();
                            int padding = Math.max(0, categoryLength * 2 - calculateDisplayWidth(categoryName));
                            template.append(String.format("%s%s  ¥%.2f\n", categoryName, " ".repeat(padding), entry.getValue()));

                        });
            }
        }

        return template.toString();
    }

    private int calculateDisplayWidth(String str) {
        if (str == null) return 0;
        int width = 0;
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
                width += 2; // 中文字符占2个字符宽度
            } else {
                width += 1; // 英文字符占1个字符宽度
            }
        }
        return width;
    }

    /**
     * 创建键盘按钮
     */
    private InlineKeyboardMarkup createKeyboardMarkup(Command command, List<Bill> bills) {
        switch (command) {
            case LAST_MONTH_SUMMARY, THIS_MONTH_SUMMARY -> {
                return new InlineKeyboardMarkup();
            }
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Bill bill : bills) {
            String billFormatted = formatBillForButton(bill);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(billFormatted);
            button.setCallbackData("bill::" + bill.getBillId());
            inlineKeyboardMarkup.addRow(button);
        }
        return inlineKeyboardMarkup;
    }


    private String formatBillForButton(Bill bill) {
        return "%s ¥%s %s %s"
                .formatted(
                        bill.getBillDate(),
                        bill.getAmount().stripTrailingZeros().toPlainString(),
                        bill.getNote(),
                        bill.getCategoryName()
                );
    }
}

