package com.tony.log4m.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 标签
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "tag", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity {

    private Integer userId;

    private String name;

    @Schema(name = "排序")
    private Integer sort;

}
