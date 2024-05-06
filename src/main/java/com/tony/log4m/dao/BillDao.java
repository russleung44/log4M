package com.tony.log4m.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tony.log4m.pojo.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tony
 * @since 2022-09-23 15:15:27
 */
@Mapper
public interface BillDao extends BaseMapper<Bill> {
    void insertBatch(@Param("list") List<Bill> list);

    void insertOrUpdateBatch(@Param("list") List<Bill> list);
}
