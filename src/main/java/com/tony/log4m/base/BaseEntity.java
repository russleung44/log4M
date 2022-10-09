package com.tony.log4m.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author TonyLeung
 * @date 2022/9/23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends Model {
    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "ID")
    private Integer id;

    @JsonIgnore
    @TableField(select = false)
    @ApiModelProperty(value = "逻辑删除字段", hidden = true)
    private Boolean deleted;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;
}
