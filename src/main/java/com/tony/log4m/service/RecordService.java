package com.tony.log4m.service;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.RecordConvert;
import com.tony.log4m.dao.AccountDao;
import com.tony.log4m.dao.RecordDao;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.dto.RecordUpdateDTO;
import com.tony.log4m.pojo.entity.Account;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Tony
 * @since 2022-09-23 15:31:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService extends CrudServiceImpl<RecordDao, Record, RecordDTO, RecordConvert> {

    private final AccountDao accountDao;


    @Override
    public RecordDTO get(Serializable id) {
        return super.get(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Serializable id) {
        Record record = Optional.ofNullable(this.getById(id)).orElseThrow();
        BigDecimal amount = record.getAmount();
        Integer accountId = record.getAccountId();

        Account account = accountDao.selectById(accountId);
        if (account == null) {
            return;
        }

        BigDecimal balance = account.getBalance();
        TransactionType transactionType = TransactionType.valueOf(record.getTransactionType());
        switch (transactionType) {
            case CONSUME -> {
                account.setBalance(balance.add(amount));
                account.setConsume(account.getConsume().subtract(amount));
            }
            case INCOME -> {
                account.setBalance(balance.subtract(amount));
                account.setIncome(account.getIncome().subtract(amount));
            }
        }

        account.updateById();
        record.deleteById();
    }


    public BigDecimal getAmountByDate(Integer userId, String date) {
        return this.query().eq("user_id", userId).eq("date", date).select("sum(amount) as amount")
                .oneOpt().map(Record::getAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getAmountByMonth(Integer userId, String lastMonth) {
        return this.query().eq("user_id", userId).likeRight("date", lastMonth).select("sum(amount) as amount")
                .oneOpt().map(Record::getAmount).orElse(BigDecimal.ZERO);
    }

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

    public RecordDTO update(RecordUpdateDTO updateDTO) {
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

        return this.get(recordId);
    }

    public void quickAdd(String text, Integer userId) {

    }
}
