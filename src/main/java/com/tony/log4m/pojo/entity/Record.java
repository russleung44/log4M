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
 * 记录
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "record", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Record extends BaseEntity {

    private Integer userId;

    private Integer accountId;

    @ApiModelProperty("交易类型 EXPENSE: 支出, INCOME: 收入")
    private String transactionType;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("分类")
    private Integer categoryId;

    @ApiModelProperty("标签")
    private Integer tagId;

    @ApiModelProperty("备注")
    private String remark;

}
