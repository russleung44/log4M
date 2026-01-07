package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Bill> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
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

        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Bill> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
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
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Bill> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.select(
                        "bill_month",
                        "sum(case when transaction_type = 'EXPENSE' then amount else 0 end) as expense",
                        "sum(case when transaction_type = 'INCOME' then amount else 0 end) as income")
                .likeRight("bill_month", year)
                .groupBy("bill_month")
                .orderByAsc("bill_month");

        return this.getBaseMapper().selectMaps(queryWrapper);
    }

}