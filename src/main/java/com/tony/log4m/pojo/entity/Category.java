package com.tony.log4m.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 记录分类
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Accessors(chain = true)
@TableName(value = "category", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity<Category> {

    private Integer userId;

    private String name;

    private Integer sort;

}
