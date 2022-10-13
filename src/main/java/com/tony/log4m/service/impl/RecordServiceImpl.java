package com.tony.log4m.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.Task;
import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.RecordConvert;
import com.tony.log4m.dao.RecordDao;
import com.tony.log4m.meili.MeiliClient;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.dto.RecordUpdateDTO;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.RecordService;
import com.tony.log4m.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class RecordServiceImpl extends CrudServiceImpl<RecordDao, Record, RecordDTO, RecordConvert> implements RecordService {


    @Override
    public RecordDTO insert(Record record) {
        RecordDTO recordDTO = super.insert(record);

        try {
            String jsonStr = JSONUtil.toJsonStr(record);
            Task task = MeiliClient.client.index("records").addDocuments(jsonStr, "id");
            System.out.println("task: " + JSONUtil.toJsonStr(task));
        } catch (Exception e) {
            log.error("写入MeiliSearch出错", e);
        }

        return recordDTO;
    }

    @Override
    public RecordDTO update(Record record) {
        Optional.ofNullable(this.getById(record.getId())).orElseThrow();

        try {
            String jsonStr = JSONUtil.toJsonStr(record);
            MeiliClient.client.index("records").updateDocuments(jsonStr, "id");

        } catch (Exception e) {
            log.error("写入MeiliSearch出错", e);
        }

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

    private void meiliSearch() throws Exception {

        SearchRequest searchRequest = new SearchRequest();
        MeiliClient.client.index("records").search(searchRequest);


    }

    @Override
    public RecordDTO update(RecordUpdateDTO updateDTO) {
        Record record = Optional.ofNullable(this.getById(updateDTO.getRecordId())).orElseThrow();
        this.update()
                .set(CommonUtil.isNotZero(updateDTO.getCategoryId()), "category_id", updateDTO.getCategoryId())
                .set(CommonUtil.isNotZero(updateDTO.getAccountId()), "account_id", updateDTO.getAccountId())
                .set(CommonUtil.isNotZero(updateDTO.getTagId()), "tag_id", updateDTO.getTagId())
                .set(StrUtil.isNotBlank(updateDTO.getTitle()), "title", updateDTO.getTitle())
                .set(StrUtil.isNotBlank(updateDTO.getDate()), "date", updateDTO.getDate())
                .set(updateDTO.getAmount() != null, "amount", updateDTO.getAmount())
                .eq("id", updateDTO.getRecordId())
                .update();
        return null;
    }

    @Override
    public void quickAdd(String text, Integer userId) {

    }
}
