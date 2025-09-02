package com.tony.log4m.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.BillMapper;
import com.tony.log4m.models.entity.Bill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

}
