package com.tony.log4m.service;

import com.github.pagehelper.PageInfo;
import com.tony.log4m.base.CrudService;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.entity.Record;

import java.math.BigDecimal;

/**
 * @author Tony
 * @since 2022-09-23 15:25:10
 */
public interface RecordService extends CrudService<Record, RecordDTO> {

    BigDecimal getAmountByDate(Integer userId, String date);

    BigDecimal getAmountByMonth(Integer userId, String lastMonth);

    PageInfo<Record> search(RecordDTO recordDTO, Integer pageNum, Integer pageSize);
}
