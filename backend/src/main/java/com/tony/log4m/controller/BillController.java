package com.tony.log4m.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.models.dto.BatchDeleteDto;
import com.tony.log4m.models.dto.CreateBillDto;
import com.tony.log4m.models.dto.UpdateBillDto;
import com.tony.log4m.models.entity.Bill;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.BillService;
import com.tony.log4m.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public List<Bill> list() {
        return billService.lambdaQuery().orderByDesc(Bill::getBillDate).last("limit 10").list();
    }

    @GetMapping("/page/{current}/{size}")
    public Page<Bill> page(@PathVariable int current, @PathVariable int size) {
        Page<Bill> page = new Page<>(current, size);
        return billService.lambdaQuery().orderByDesc(Bill::getBillDate).orderByDesc(Bill::getBillId).page(page);
    }

    /**
     * 分页查询账单（新增前端API）
     */
    @GetMapping
    public ResultVO<Page<Bill>> pageQuery(
            @RequestParam(name = "current", defaultValue = "1") int current,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "accountId", required = false) Long accountId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "transactionType", required = false) TransactionType transactionType,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "minAmount", required = false) java.math.BigDecimal minAmount,
            @RequestParam(name = "maxAmount", required = false) java.math.BigDecimal maxAmount) {
        Page<Bill> page = new Page<>(current, size);

        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();

        // 处理分类筛选：-1 表示查询未分类的账单（categoryId 为 0）
        if (categoryId != null && categoryId == -1) {
            queryWrapper.eq(Bill::getCategoryId, 0);
        } else if (categoryId != null) {
            queryWrapper.eq(Bill::getCategoryId, categoryId);
        }

        queryWrapper.eq(accountId != null, Bill::getAccountId, accountId)
                .ge(startDate != null, Bill::getBillDate, startDate)
                .le(endDate != null, Bill::getBillDate, endDate)
                .eq(transactionType != null, Bill::getTransactionType, transactionType)
                .ge(minAmount != null, Bill::getAmount, minAmount)
                .le(maxAmount != null, Bill::getAmount, maxAmount)
                .like(keyword != null && !keyword.trim().isEmpty(), Bill::getNote, keyword)
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getBillId);

        Page<Bill> result = billService.page(page, queryWrapper);
        return ResultVO.success(result);
    }

    /**
     * 创建账单
     */
    @PostMapping
    public ResultVO<Bill> create(@Valid @RequestBody CreateBillDto dto) {
        Bill bill = new Bill();
        bill.setBillDate(dto.getBillDate());
        bill.setAmount(dto.getAmount());
        bill.setNote(dto.getNote());
        bill.setCategoryId(dto.getCategoryId());
        bill.setAccountId(dto.getAccountId());
        bill.setTagId(dto.getTagId());
        bill.setTransactionType(dto.getTransactionType());

        // 同步设置分类名称
        if (dto.getCategoryId() != null) {
            try {
                Category category = categoryService.getById(dto.getCategoryId());
                bill.setCategoryName(category.getCategoryName());
                bill.setParentCategoryId(category.getParentCategoryId());

                // 只有当 parentCategoryId 不为 null 且不为 0 时才查询父分类
                if (category.getParentCategoryId() != null && category.getParentCategoryId() != 0) {
                    Category parentCategory = categoryService.getById(category.getParentCategoryId());
                    bill.setParentCategoryName(parentCategory.getCategoryName());
                } else {
                    bill.setParentCategoryName(null);
                }
            } catch (Exception e) {
                // 如果分类不存在，设置为默认值
                bill.setCategoryName("未分类");
                bill.setParentCategoryName(null);
            }
        } else {
            bill.setCategoryName("未分类");
            bill.setParentCategoryName(null);
        }

        boolean saved = billService.save(bill);
        if (saved) {
            return ResultVO.success(bill);
        } else {
            return ResultVO.error("创建账单失败");
        }
    }

    /**
     * 更新账单
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, @Valid @RequestBody UpdateBillDto dto) {
        Bill bill = billService.getById(id);
        if (bill == null) {
            return ResultVO.error("账单不存在");
        }

        bill.setBillDate(dto.getBillDate());
        bill.setAmount(dto.getAmount());
        bill.setNote(dto.getNote());
        bill.setCategoryId(dto.getCategoryId());
        bill.setAccountId(dto.getAccountId());
        bill.setTagId(dto.getTagId());
        bill.setTransactionType(dto.getTransactionType());

        // 同步更新分类名称
        if (dto.getCategoryId() != null) {
            try {
                Category category = categoryService.getById(dto.getCategoryId());
                bill.setCategoryName(category.getCategoryName());
                bill.setParentCategoryId(category.getParentCategoryId());

                // 只有当 parentCategoryId 不为 null 且不为 0 时才查询父分类
                if (category.getParentCategoryId() != null && category.getParentCategoryId() != 0) {
                    Category parentCategory = categoryService.getById(category.getParentCategoryId());
                    bill.setParentCategoryName(parentCategory.getCategoryName());
                } else {
                    bill.setParentCategoryName(null);
                }
            } catch (Exception e) {
                // 如果分类不存在，设置为默认值
                bill.setCategoryName("未分类");
                bill.setParentCategoryName(null);
            }
        } else {
            bill.setCategoryName("未分类");
            bill.setParentCategoryName(null);
        }

        boolean updated = billService.updateById(bill);
        return ResultVO.success(updated);
    }

    /**
     * 删除账单
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id) {
        boolean deleted = billService.removeById(id);
        return ResultVO.success(deleted);
    }

    /**
     * 批量删除账单
     */
    @DeleteMapping("/batch")
    public ResultVO<Boolean> batchDelete(@Valid @RequestBody BatchDeleteDto dto) {
        boolean deleted = billService.removeByIds(dto.getIds());
        return ResultVO.success(deleted);
    }

    /**
     * 获取日统计
     */
    @GetMapping("/statistics/daily")
    public ResultVO<Map<String, Object>> getDailyStatistics(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Map<String, Object> result = new HashMap<>();

        // 当日收入
        BigDecimal income = billService.lambdaQuery()
                .eq(Bill::getBillDate, date)
                .eq(Bill::getTransactionType, TransactionType.INCOME)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 当日支出
        BigDecimal expense = billService.lambdaQuery()
                .eq(Bill::getBillDate, date)
                .eq(Bill::getTransactionType, TransactionType.EXPENSE)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 当日交易笔数
        Long count = billService.lambdaQuery()
                .eq(Bill::getBillDate, date)
                .count();

        result.put("date", date);
        result.put("income", income);
        result.put("expense", expense);
        result.put("balance", income.subtract(expense));
        result.put("count", count);

        return ResultVO.success(result);
    }

    /**
     * 获取月统计
     */
    @GetMapping("/statistics/monthly")
    public ResultVO<Map<String, Object>> getMonthlyStatistics(
            @RequestParam(name = "month") String month // 格式: yyyy-MM
    ) {
        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Map<String, Object> result = new HashMap<>();

        // 月收入
        BigDecimal income = billService.lambdaQuery()
                .ge(Bill::getBillDate, startDate)
                .le(Bill::getBillDate, endDate)
                .eq(Bill::getTransactionType, TransactionType.INCOME)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 月支出
        BigDecimal expense = billService.lambdaQuery()
                .ge(Bill::getBillDate, startDate)
                .le(Bill::getBillDate, endDate)
                .eq(Bill::getTransactionType, TransactionType.EXPENSE)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 月交易笔数
        Long count = billService.lambdaQuery()
                .ge(Bill::getBillDate, startDate)
                .le(Bill::getBillDate, endDate)
                .count();

        result.put("month", month);
        result.put("income", income);
        result.put("expense", expense);
        result.put("balance", income.subtract(expense));
        result.put("count", count);

        return ResultVO.success(result);
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/statistics/category")
    public ResultVO<List<Map<String, Object>>> getCategoryStatistics(
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "transactionType", required = false) TransactionType transactionType) {
        List<Map<String, Object>> result = billService.getCategoryStatistics(startDate, endDate, transactionType);
        return ResultVO.success(result);
    }

    /**
     * 获取最近N天的趋势数据
     */
    @GetMapping("/statistics/trend")
    public ResultVO<List<Map<String, Object>>> getTrendStatistics(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        List<Map<String, Object>> result = billService.getTrendStatistics(days);
        return ResultVO.success(result);
    }

    /**
     * 获取年统计
     */
    @GetMapping("/statistics/yearly")
    public ResultVO<Map<String, Object>> getYearlyStatistics(
            @RequestParam(name = "year") String year // 格式: yyyy
    ) {
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate endDate = startDate.plusYears(1).minusDays(1);

        Map<String, Object> result = new HashMap<>();

        // 年支出
        BigDecimal expense = billService.lambdaQuery()
                .ge(Bill::getBillDate, startDate)
                .le(Bill::getBillDate, endDate)
                .eq(Bill::getTransactionType, TransactionType.EXPENSE)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("year", year);
        result.put("expense", expense);

        return ResultVO.success(result);
    }

    /**
     * 获取年支出统计（仅支出）
     */
    @GetMapping("/statistics/yearly/expense")
    public ResultVO<Map<String, Object>> getYearlyExpenseStatistics(
            @RequestParam(name = "year") String year // 格式: yyyy
    ) {
        LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
        LocalDate endDate = startDate.plusYears(1).minusDays(1);

        Map<String, Object> result = new HashMap<>();

        // 年支出
        BigDecimal expense = billService.lambdaQuery()
                .ge(Bill::getBillDate, startDate)
                .le(Bill::getBillDate, endDate)
                .eq(Bill::getTransactionType, TransactionType.EXPENSE)
                .list()
                .stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("year", year);
        result.put("expense", expense);

        return ResultVO.success(result);
    }

    /**
     * 获取年度月度支出统计
     */
    @GetMapping("/statistics/yearly/monthly")
    public ResultVO<List<Map<String, Object>>> getYearlyMonthlyStatistics(
            @RequestParam(name = "year") String year // 格式: yyyy
    ) {
        List<Map<String, Object>> result = billService.getYearlyMonthlyStatistics(year);
        return ResultVO.success(result);
    }
}
