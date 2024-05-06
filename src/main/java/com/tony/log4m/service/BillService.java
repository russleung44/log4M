package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.log4m.dao.AccountDao;
import com.tony.log4m.dao.BillDao;
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
public class BillService extends ServiceImpl<BillDao, Bill> {


    public BigDecimal getAmountByDate(String day) {
        return this.query().eq("day", day).select("sum(amount) as amount")
                .oneOpt().map(Bill::getAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAmountByMonth(String month) {
        return this.query().likeRight("month", month).select("sum(amount) as amount")
                .oneOpt().map(Bill::getAmount).orElse(BigDecimal.ZERO);
    }

    public List<Bill> search(RecordDTO recordDTO, Integer pageNum, Integer pageSize) {
        Integer tagId = recordDTO.getTagId();
        Integer accountId = recordDTO.getAccountId();
        Integer categoryId = recordDTO.getCategoryId();
        String transactionType = recordDTO.getTransactionType();

        List<Bill> list = this.query()
                .eq(CommonUtil.isNotZero(tagId), "tag_id", tagId)
                .eq(CommonUtil.isNotZero(accountId), "account_id", accountId)
                .eq(CommonUtil.isNotZero(categoryId), "category_id", categoryId)
                .eq(StrUtil.isNotBlank(transactionType), "transaction_type", transactionType)
                .list();
        return list;
    }

    public void update(RecordUpdateDTO updateDTO) {
        Integer recordId = updateDTO.getRecordId();
        this.update()
                .set(CommonUtil.isNotZero(updateDTO.getCategoryId()), "category_id", updateDTO.getCategoryId())
                .set(CommonUtil.isNotZero(updateDTO.getAccountId()), "account_id", updateDTO.getAccountId())
                .set(CommonUtil.isNotZero(updateDTO.getTagId()), "tag_id", updateDTO.getTagId())
                .set(StrUtil.isNotBlank(updateDTO.getTitle()), "title", updateDTO.getTitle())
                .set(StrUtil.isNotBlank(updateDTO.getDate()), "date", updateDTO.getDate())
                .set(updateDTO.getAmount() != null, "amount", updateDTO.getAmount())
                .eq("id", recordId)
                .update();

    }

    public void quickAdd(String text, Integer userId) {

    }
}
