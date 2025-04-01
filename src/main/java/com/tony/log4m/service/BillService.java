package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.mapper.BillMapper;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.dto.RecordUpdateDTO;
import com.tony.log4m.pojo.entity.Bill;
import com.tony.log4m.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public List<Bill> search(RecordDTO recordDTO, Integer pageNum, Integer pageSize) {
        Integer tagId = recordDTO.getTagId();
        Integer accountId = recordDTO.getAccountId();
        Integer categoryId = recordDTO.getCategoryId();
        String transactionType = recordDTO.getTransactionType();

        List<Bill> list = this.lambdaQuery()
                .eq(CommonUtil.isNotZero(tagId), Bill::getTagId, tagId)
                .eq(CommonUtil.isNotZero(accountId), Bill::getAccountId, accountId)
                .eq(CommonUtil.isNotZero(categoryId), Bill::getCategoryId, categoryId)
                .eq(StrUtil.isNotBlank(transactionType), Bill::getTransactionType, transactionType)
                .list();
        return list;
    }

    public void update(RecordUpdateDTO updateDTO) {
        Integer recordId = updateDTO.getRecordId();
        this.lambdaUpdate()
                .set(CommonUtil.isNotZero(updateDTO.getCategoryId()), Bill::getCategoryId, updateDTO.getCategoryId())
                .set(CommonUtil.isNotZero(updateDTO.getAccountId()), Bill::getAccountId, updateDTO.getAccountId())
                .set(CommonUtil.isNotZero(updateDTO.getTagId()), Bill::getTagId, updateDTO.getTagId())
                .set(StrUtil.isNotBlank(updateDTO.getTitle()), Bill::getTitle, updateDTO.getTitle())
                .set(StrUtil.isNotBlank(updateDTO.getDate()), Bill::getBillDate, updateDTO.getDate())
                .set(updateDTO.getAmount() != null, Bill::getAmount, updateDTO.getAmount())
                .eq(Bill::getId, recordId)
                .update();

    }

    public void quickAdd(String text, Integer userId) {

    }
}
