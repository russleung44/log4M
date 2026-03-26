package com.tony.log4m.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.log4m.models.entity.Bill;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * @author Tony
 * @since 2022-09-23 15:15:27
 */
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 查询已删除的账单（绕过逻辑删除，按修改时间即删除时间降序）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM bill WHERE deleted = 1 ORDER BY md_time DESC, bill_id DESC")
    IPage<Bill> selectTrashPage(Page<Bill> page);

    /**
     * 彻底删除账单（绕过逻辑删除）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Delete("DELETE FROM bill WHERE bill_id = #{id}")
    int permanentDeleteById(@Param("id") Long id);

    /**
     * 恢复账单
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update("UPDATE bill SET deleted = 0 WHERE bill_id = #{id}")
    int restoreById(@Param("id") Long id);

    /**
     * 批量恢复账单
     */
    @InterceptorIgnore(tenantLine = "true")
    @Update("<script>UPDATE bill SET deleted = 0 WHERE bill_id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int batchRestoreByIds(@Param("ids") List<Long> ids);

    /**
     * 批量彻底删除账单
     */
    @InterceptorIgnore(tenantLine = "true")
    @Delete("<script>DELETE FROM bill WHERE bill_id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int batchPermanentDeleteByIds(@Param("ids") List<Long> ids);
}
