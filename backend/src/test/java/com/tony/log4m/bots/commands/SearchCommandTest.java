package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.Command;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SearchCommand 单元测试
 *
 * @author Tony
 * @since 9/4/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchCommand 测试")
class SearchCommandTest {

    private static final Long CHAT_ID = 1L;

    @Mock
    private BillService billService;

    private SearchCommand searchCommand;

    @BeforeEach
    void setUp() {
        searchCommand = new SearchCommand(billService);
    }

    @Test
    @DisplayName("空关键词 - 返回用法提示")
    void testExecute_WhenKeywordIsBlank() {
        // When
        SendMessage message = searchCommand.execute(Command.SEARCH, "  ", CHAT_ID);

        // Then
        assertTrue(textOf(message).contains("请输入搜索关键词"));
    }

    @Test
    @DisplayName("无匹配结果 - 返回未找到提示")
    void testExecute_WhenNoResult() {
        // Given
        when(billService.searchByNote("zzz不存在")).thenReturn(Collections.emptyList());

        // When
        SendMessage message = searchCommand.execute(Command.SEARCH, "zzz不存在", CHAT_ID);

        // Then
        assertTrue(textOf(message).contains("未找到备注包含\"zzz不存在\"的账单"));
        verify(billService).searchByNote("zzz不存在");
    }

    @Test
    @DisplayName("命中超过30条 - 仅显示最近30条并提示溢出")
    void testExecute_TruncateToDisplayLimit() {
        // Given
        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < 35; i++) {
            bills.add(bill("奶茶", "饮品", new BigDecimal("10"), TransactionType.EXPENSE));
        }
        when(billService.searchByNote("奶茶")).thenReturn(bills);

        // When
        String text = textOf(searchCommand.execute(Command.SEARCH, "奶茶", CHAT_ID));

        // Then
        assertTrue(text.contains("共35条"));
        assertTrue(text.contains("30. "));
        assertFalse(text.contains("31. "));
        assertTrue(text.contains("…… 共35条，仅显示最近30条"));
    }

    @Test
    @DisplayName("混合收支 - 支出收入分开统计且收入行带标记")
    void testExecute_MixedIncomeAndExpense() {
        // Given
        List<Bill> bills = List.of(
                bill("奶茶", "饮品", new BigDecimal("25.00"), TransactionType.EXPENSE),
                bill("奶茶", "饮品", new BigDecimal("15.00"), TransactionType.EXPENSE),
                bill("奶茶退款", "饮品格", new BigDecimal("100.00"), TransactionType.INCOME)
        );
        when(billService.searchByNote("奶茶")).thenReturn(bills);

        // When
        String text = textOf(searchCommand.execute(Command.SEARCH, "奶茶", CHAT_ID));

        // Then
        assertTrue(text.contains("支出：¥40"));
        assertTrue(text.contains("收入：¥100"));
        assertTrue(text.contains("🔺"));
    }

    @Test
    @DisplayName("超长备注 - 消息长度不超过Telegram上限")
    void testExecute_MessageLengthGuard() {
        // Given
        String longNote = "长".repeat(255);
        String longCategory = "类".repeat(255);
        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            bills.add(bill(longNote, longCategory, new BigDecimal("12.5"), TransactionType.EXPENSE));
        }
        when(billService.searchByNote(longNote)).thenReturn(bills);

        // When
        String text = textOf(searchCommand.execute(Command.SEARCH, longNote, CHAT_ID));

        // Then
        assertTrue(text.contains("1. "));
        assertTrue(text.length() < 4096);
    }

    /**
     * 读取 SendMessage 中的文本内容
     */
    private String textOf(SendMessage message) {
        return String.valueOf(message.getParameters().get("text"));
    }

    /**
     * 构建测试账单
     */
    private Bill bill(String note, String categoryName, BigDecimal amount, TransactionType type) {
        return Bill.builder()
                .billDate(LocalDate.of(2025, 8, 31))
                .transactionType(type)
                .amount(amount)
                .note(note)
                .categoryName(categoryName)
                .build();
    }
}
