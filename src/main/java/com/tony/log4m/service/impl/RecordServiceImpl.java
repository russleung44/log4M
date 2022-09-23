package com.tony.log4m.service.impl;


import com.tony.log4m.base.CrudServiceImpl;
import com.tony.log4m.convert.RecordConvert;
import com.tony.log4m.dao.RecordDao;
import com.tony.log4m.pojo.dto.RecordDTO;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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

}
