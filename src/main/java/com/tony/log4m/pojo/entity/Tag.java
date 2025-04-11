package com.tony.log4m.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 标签
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Accessors(chain = true)
@TableName(value = "tag", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity<Tag> {

    private String name;

    private Integer sort;

}
