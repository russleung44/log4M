package com.tony.log4m.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Date;

/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends Model {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(name = "ID")
    private Integer id;

    @JsonIgnore
    @TableField(select = false)
    @Schema(name = "逻辑删除字段", hidden = true)
    private Boolean deleted;

    @Schema(name = "创建时间")
    private Date createTime;

    @Schema(name = "更新时间", hidden = true)
    private Date updateTime;
}
