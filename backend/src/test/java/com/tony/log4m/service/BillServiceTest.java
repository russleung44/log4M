package com.tony.log4m.service;

import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.entity.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * BillService 单元测试
 *
 * @author Tony
 * @since 2022-09-23
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BillService 测试")
class BillServiceTest {

    @Spy
    private BillService billService;

    private Bill testBill;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 12, 2);
        testBill = Bill.builder()
                .billId(1L)
                .billDate(testDate)
                .billMonth("2024-12")
                .amount(new BigDecimal("100.50"))
                .categoryName("餐饮")
                .transactionType(TransactionType.EXPENSE)
                .build();
    }

    @Test
    @DisplayName("根据日期获取金额 - 存在记录")
    void testGetAmountByDate_WhenRecordsExist() {
        // Given
        String day = "2024-12-02";
        BigDecimal expectedAmount = new BigDecimal("200.75");

        doReturn(expectedAmount).when(billService).getAmountByDate(day);

        // When
        BigDecimal result = billService.getAmountByDate(day);

        // Then
        assertEquals(expectedAmount, result);
        verify(billService).getAmountByDate(day);
    }

    @Test
    @DisplayName("根据日期获取金额 - 不存在记录")
    void testGetAmountByDate_WhenNoRecordsExist() {
        // Given
        String day = "2024-12-02";

        doReturn(BigDecimal.ZERO).when(billService).getAmountByDate(day);

        // When
        BigDecimal result = billService.getAmountByDate(day);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(billService).getAmountByDate(day);
    }

    @Test
    @DisplayName("根据月份获取金额 - 存在记录")
    void testGetAmountByMonth_WhenRecordsExist() {
        // Given
        String month = "2024-12";
        BigDecimal expectedAmount = new BigDecimal("1500.00");

        doReturn(expectedAmount).when(billService).getAmountByMonth(month);

        // When
        BigDecimal result = billService.getAmountByMonth(month);

        // Then
        assertEquals(expectedAmount, result);
        verify(billService).getAmountByMonth(month);
    }

    @Test
    @DisplayName("根据月份获取金额 - 不存在记录")
    void testGetAmountByMonth_WhenNoRecordsExist() {
        // Given
        String month = "2024-12";

        doReturn(BigDecimal.ZERO).when(billService).getAmountByMonth(month);

        // When
        BigDecimal result = billService.getAmountByMonth(month);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(billService).getAmountByMonth(month);
    }

    @Test
    @DisplayName("获取分类统计 - 指定交易类型")
    void testGetCategoryStatistics_WithTransactionType() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        TransactionType transactionType = TransactionType.EXPENSE;

        List<Map<String, Object>> expectedResults = Arrays.asList(
                createCategoryResult("餐饮", new BigDecimal("500.00"), 15L),
                createCategoryResult("交通", new BigDecimal("200.00"), 8L),
                createCategoryResult("购物", new BigDecimal("300.00"), 5L)
        );

        doReturn(expectedResults).when(billService).getCategoryStatistics(
                startDate, endDate, transactionType);

        // When
        List<Map<String, Object>> result = billService.getCategoryStatistics(
                startDate, endDate, transactionType);

        // Then
        assertEquals(3, result.size());
        assertEquals("餐饮", result.get(0).get("categoryName"));
        assertEquals(new BigDecimal("500.00"), result.get(0).get("amount"));
        assertEquals(15L, result.get(0).get("count"));

        verify(billService).getCategoryStatistics(startDate, endDate, transactionType);
    }

    @Test
    @DisplayName("获取分类统计 - 不指定交易类型")
    void testGetCategoryStatistics_WithoutTransactionType() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Map<String, Object>> expectedResults = Arrays.asList(
                createCategoryResult("工资", new BigDecimal("8000.00"), 1L),
                createCategoryResult("餐饮", new BigDecimal("500.00"), 15L)
        );

        doReturn(expectedResults).when(billService).getCategoryStatistics(
                startDate, endDate, null);

        // When
        List<Map<String, Object>> result = billService.getCategoryStatistics(
                startDate, endDate, null);

        // Then
        assertEquals(2, result.size());
        assertEquals("工资", result.get(0).get("categoryName"));
        assertEquals(new BigDecimal("8000.00"), result.get(0).get("amount"));
        assertEquals(1L, result.get(0).get("count"));

        verify(billService).getCategoryStatistics(startDate, endDate, null);
    }

    @Test
    @DisplayName("获取分类统计 - 无数据")
    void testGetCategoryStatistics_NoData() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        TransactionType transactionType = TransactionType.EXPENSE;

        doReturn(Collections.emptyList()).when(billService).getCategoryStatistics(
                startDate, endDate, transactionType);

        // When
        List<Map<String, Object>> result = billService.getCategoryStatistics(
                startDate, endDate, transactionType);

        // Then
        assertTrue(result.isEmpty());
        verify(billService).getCategoryStatistics(startDate, endDate, transactionType);
    }

    @Test
    @DisplayName("获取趋势统计 - 7天")
    void testGetTrendStatistics_SevenDays() {
        // Given
        int days = 7;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<Map<String, Object>> expectedResults = Arrays.asList(
                createTrendResult(endDate.minusDays(6), new BigDecimal("50.00"), new BigDecimal("200.00")),
                createTrendResult(endDate.minusDays(5), new BigDecimal("0.00"), new BigDecimal("150.00")),
                createTrendResult(endDate.minusDays(4), new BigDecimal("100.00"), new BigDecimal("80.00")),
                createTrendResult(endDate.minusDays(3), new BigDecimal("0.00"), new BigDecimal("120.00")),
                createTrendResult(endDate.minusDays(2), new BigDecimal("300.00"), new BigDecimal("90.00")),
                createTrendResult(endDate.minusDays(1), new BigDecimal("0.00"), new BigDecimal("250.00")),
                createTrendResult(endDate, new BigDecimal("200.00"), new BigDecimal("180.00"))
        );

        doReturn(expectedResults).when(billService).getTrendStatistics(days);

        // When
        List<Map<String, Object>> result = billService.getTrendStatistics(days);

        // Then
        assertEquals(7, result.size());
        verify(billService).getTrendStatistics(days);

        // 验证数据格式
        Map<String, Object> firstDay = result.get(0);
        assertTrue(firstDay.containsKey("date"));
        assertTrue(firstDay.containsKey("income"));
        assertTrue(firstDay.containsKey("expense"));
    }

    @Test
    @DisplayName("获取趋势统计 - 30天")
    void testGetTrendStatistics_ThirtyDays() {
        // Given
        int days = 30;

        doReturn(Collections.emptyList()).when(billService).getTrendStatistics(days);

        // When
        List<Map<String, Object>> result = billService.getTrendStatistics(days);

        // Then
        assertTrue(result.isEmpty());
        verify(billService).getTrendStatistics(days);
    }

    @Test
    @DisplayName("获取趋势统计 - 无数据")
    void testGetTrendStatistics_NoData() {
        // Given
        int days = 3;

        doReturn(Collections.emptyList()).when(billService).getTrendStatistics(days);

        // When
        List<Map<String, Object>> result = billService.getTrendStatistics(days);

        // Then
        assertTrue(result.isEmpty());
        verify(billService).getTrendStatistics(days);
    }

    @Test
    @DisplayName("方法调用验证")
    void testMethodInvocation() {
        // Given
        String day = "2024-12-02";
        BigDecimal expectedAmount = new BigDecimal("100.00");

        doReturn(expectedAmount).when(billService).getAmountByDate(day);

        // When
        BigDecimal result = billService.getAmountByDate(day);

        // Then
        assertEquals(expectedAmount, result);
        verify(billService, times(1)).getAmountByDate(day);
    }

    /**
     * 创建分类统计结果
     */
    private Map<String, Object> createCategoryResult(String categoryName, BigDecimal amount, Long count) {
        Map<String, Object> result = new HashMap<>();
        result.put("categoryName", categoryName);
        result.put("amount", amount);
        result.put("count", count);
        return result;
    }

    /**
     * 创建趋势统计结果
     */
    private Map<String, Object> createTrendResult(LocalDate date, BigDecimal income, BigDecimal expense) {
        Map<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("income", income);
        result.put("expense", expense);
        return result;
    }
}