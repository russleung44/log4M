package com.tony.log4m.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.StrUtil;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.mapper.BillMapper;
import com.tony.log4m.models.entity.Bill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BillService extends ServiceImpl<BillMapper, Bill> {


    public BigDecimal getAmountByDate(String day) {
        return this.query().eq("bill_day", day).select("sum(amount) as amount")
                .oneOpt().map(Bill::getAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAmountByMonth(String month) {
        return this.query().likeRight("bill_month", month).select("sum(amount) as amount")
                .oneOpt().map(Bill::getAmount).orElse(BigDecimal.ZERO);
    }
    
    /**
     * 获取分类统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param transactionType 交易类型
     * @return 分类统计结果
     */
    public List<Map<String, Object>> getCategoryStatistics(LocalDate startDate, LocalDate endDate, TransactionType transactionType) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("category_name as categoryName", "sum(amount) as amount", "count(*) as count")
                .ge("bill_date", startDate)
                .le("bill_date", endDate)
                .groupBy("category_name");
        
        if (transactionType != null) {
            queryWrapper.eq("transaction_type", transactionType);
        }
        
        return this.getBaseMapper().selectMaps(queryWrapper);
    }
    
    /**
     * 获取趋势统计数据
     * @param days 天数
     * @return 趋势统计数据
     */
    public List<Map<String, Object>> getTrendStatistics(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("bill_date as date",
                "sum(case when transaction_type = 'INCOME' then amount else 0 end) as income",
                "sum(case when transaction_type = 'EXPENSE' then amount else 0 end) as expense")
                .ge("bill_date", startDate)
                .le("bill_date", endDate)
                .groupBy("bill_date")
                .orderByAsc("bill_date");

        return this.getBaseMapper().selectMaps(queryWrapper);
    }

    /**
     * 获取年度月度支出统计
     * @param year 年份（格式: yyyy）
     * @return 年度月度统计数据
     */
    public List<Map<String, Object>> getYearlyMonthlyStatistics(String year) {
        QueryWrapper<Bill> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                        "bill_month",
                        "sum(case when transaction_type = 'EXPENSE' then amount else 0 end) as expense",
                        "sum(case when transaction_type = 'INCOME' then amount else 0 end) as income")
                .likeRight("bill_month", year)
                .groupBy("bill_month")
                .orderByAsc("bill_month");

        return this.getBaseMapper().selectMaps(queryWrapper);
    }

    /**
     * 从账单派生规则关键词
     * 优先级: note > remark > categoryName
     */
    public static String deriveKeyword(Bill bill) {
        if (StrUtil.isNotBlank(bill.getNote())) return bill.getNote();
        if (StrUtil.isNotBlank(bill.getRemark())) return bill.getRemark();
        if (StrUtil.isNotBlank(bill.getCategoryName())) return bill.getCategoryName();
        return null;
    }

    // ==================== 回收站相关方法 ====================

    /**
     * 获取回收站账单列表
     */
    public Page<Bill> getTrashBills(Page<Bill> page) {
        return (Page<Bill>) this.getBaseMapper().selectTrashPage(page);
    }

    /**
     * 恢复账单
     */
    public boolean restoreBill(Long id) {
        return this.getBaseMapper().restoreById(id) > 0;
    }

    /**
     * 彻底删除账单
     */
    public boolean permanentDeleteBill(Long id) {
        return this.getBaseMapper().permanentDeleteById(id) > 0;
    }

    /**
     * 批量恢复账单
     */
    public boolean batchRestoreBills(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return this.getBaseMapper().batchRestoreByIds(ids) > 0;
    }

    /**
     * 批量彻底删除账单
     */
    public boolean batchPermanentDeleteBills(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return this.getBaseMapper().batchPermanentDeleteByIds(ids) > 0;
    }

}