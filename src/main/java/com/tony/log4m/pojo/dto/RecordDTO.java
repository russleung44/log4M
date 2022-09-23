package com.tony.log4m.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author Tony
 * @since 2022-09-23 15:13:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class RecordDTO {

    @ApiModelProperty("")
    private Integer id;

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
