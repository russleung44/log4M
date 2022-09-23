package com.tony.log4m.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tony.log4m.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tony
 * @since 2022-09-23 15:15:27
 */
@Mapper
public interface CategoryDao extends BaseMapper<Category> {
    void insertBatch(@Param("list") List<Category> list);

    void insertOrUpdateBatch(@Param("list") List<Category> list);
}
