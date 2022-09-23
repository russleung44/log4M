package com.tony.log4m.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tony.log4m.pojo.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tony
 * @since 2022-09-23 15:15:27
 */
@Mapper
public interface RecordDao extends BaseMapper<Record> {
    void insertBatch(@Param("list") List<Record> list);

    void insertOrUpdateBatch(@Param("list") List<Record> list);
}
