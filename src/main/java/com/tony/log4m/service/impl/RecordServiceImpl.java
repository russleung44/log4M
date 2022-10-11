package com.tony.log4m.service.impl;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.RecordConvert;
import com.tony.log4m.dao.RecordDao;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.RecordService;
import com.tony.log4m.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Service
@RequiredArgsConstructor
public class RecordServiceImpl extends CrudServiceImpl<RecordDao, Record, RecordDTO, RecordConvert> implements RecordService {

    @Override
    public RecordDTO insert(Record record) {
        return super.insert(record);
    }

    @Override
    public RecordDTO update(Record record) {
        Optional.ofNullable(this.getById(record.getId())).orElseThrow();
        return super.update(record);
    }

    @Override
    public RecordDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    public void delete(Serializable id) {
        super.delete(id);
    }

    @Override
    public BigDecimal getAmountByDate(Integer userId, String date) {
        return this.query().eq("user_id", userId).eq("date", date).select("sum(amount) as amount")
                .oneOpt().map(Record::getAmount).orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAmountByMonth(Integer userId, String lastMonth) {
        return this.query().eq("user_id", userId).likeRight("date", lastMonth).select("sum(amount) as amount")
                .oneOpt().map(Record::getAmount).orElse(BigDecimal.ZERO);
    }

    @Override
    public PageInfo<Record> search(RecordDTO recordDTO, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Integer tagId = recordDTO.getTagId();
        Integer accountId = recordDTO.getAccountId();
        Integer categoryId = recordDTO.getCategoryId();
        String transactionType = recordDTO.getTransactionType();
        List<Record> list = this.query()
                .eq("user_id", recordDTO.getUserId())
                .eq(CommonUtil.isNotZero(tagId), "tag_id", tagId)
                .eq(CommonUtil.isNotZero(accountId), "account_id", accountId)
                .eq(CommonUtil.isNotZero(categoryId), "category_id", categoryId)
                .eq(StrUtil.isNotBlank(transactionType), "transaction_type", transactionType)
                .list();
        return PageInfo.of(list);
    }
}
