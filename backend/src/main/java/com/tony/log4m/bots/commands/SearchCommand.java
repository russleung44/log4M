package com.tony.log4m.bots.commands;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.service.BillService;
import com.tony.log4m.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 按备注关键词搜索账单：/search 关键词
 *
 * @author Tony
 * @since 9/4/2026
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchCommand implements CommandStrategy {

    /**
     * 最多展示条数（对齐 RuleCommand 的截断先例）
     */
    private static final int DISPLAY_LIMIT = 30;

    /**
     * 消息长度护栏（Telegram 上限 4096，留余量）
     */
    private static final int MAX_MESSAGE_LENGTH = 3500;

    private final BillService billService;

    @Override
    public SendMessage execute(Command command, String param, Long chatId) {
        String keyword = StrUtil.trim(param);
        if (StrUtil.isBlank(keyword)) {
            return new SendMessage(chatId, "请输入搜索关键词，例如：/search 奶茶");
        }

        List<Bill> bills = billService.searchByNote(keyword);
        if (CollUtil.isEmpty(bills)) {
            return new SendMessage(chatId, "未找到备注包含\"" + keyword + "\"的账单");
        }

        return new SendMessage(chatId, generateTemplate(keyword, bills));
    }

    /**
     * 生成搜索结果消息
     */
    private String generateTemplate(String keyword, List<Bill> bills) {
        BigDecimal expense = sumByType(bills, TransactionType.EXPENSE);
        BigDecimal income = sumByType(bills, TransactionType.INCOME);

        StringBuilder text = new StringBuilder();
        text.append("🔍 搜索\"").append(keyword).append("\"：共")
                .append(bills.size()).append("条\n");
        text.append("支出：¥").append(MoneyUtil.formatBigDecimal(expense));
        if (income.compareTo(BigDecimal.ZERO) > 0) {
            text.append("  收入：¥").append(MoneyUtil.formatBigDecimal(income));
        }
        text.append('\n').append("——————————————\n");

        int shown = 0;
        for (Bill bill : bills) {
            if (shown >= DISPLAY_LIMIT) {
                break;
            }
            String row = formatBillRow(bill);
            if (text.length() + row.length() > MAX_MESSAGE_LENGTH) {
                break;
            }
            text.append(shown + 1).append(". ").append(row);
            shown++;
        }
        if (shown < bills.size()) {
            text.append("…… 共").append(bills.size())
                    .append("条，仅显示最近").append(shown).append("条\n");
        }
        return text.toString();
    }

    /**
     * 按交易类型汇总金额
     */
    private BigDecimal sumByType(List<Bill> bills, TransactionType type) {
        return bills.stream()
                .filter(b -> b.getTransactionType() == type)
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 单条账单行：日期  ¥金额  备注  [分类]，收入加🔺前缀
     */
    private String formatBillRow(Bill bill) {
        String categoryName = StrUtil.blankToDefault(bill.getCategoryName(), "未分类");
        String row = String.format("%s  ¥%s  %s  [%s]\n",
                bill.getBillDate(),
                MoneyUtil.formatBigDecimal(bill.getAmount()),
                StrUtil.nullToEmpty(bill.getNote()),
                categoryName);
        return bill.getTransactionType() == TransactionType.INCOME ? "🔺" + row : row;
    }
}
