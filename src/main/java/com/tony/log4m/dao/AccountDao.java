package com.tony.log4m.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tony.log4m.pojo.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tony
 * @since 2022-09-23 15:15:26
 */
@Mapper
public interface AccountDao extends BaseMapper<Account> {
    void insertBatch(@Param("list") List<Account> list);

    void insertOrUpdateBatch(@Param("list") List<Account> list);
}
