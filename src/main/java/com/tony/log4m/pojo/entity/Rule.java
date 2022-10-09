package com.tony.log4m.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 规则
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "rule", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Rule extends BaseEntity {

    @ApiModelProperty("规则名称")
    private String name;

    private Integer userId;

    private Integer accountId;

    @ApiModelProperty("交易类型 EXPENSE: 支出, INCOME: 收入")
    private String transactionType;

    @ApiModelProperty("分类")
    private Integer categoryId;

    @ApiModelProperty("标签")
    private Integer tagId;

    @ApiModelProperty("默认金额")
    private BigDecimal amount;

    @ApiModelProperty("关键字")
    private String keywords;

    @ApiModelProperty("排序")
    private Integer sort;

}
