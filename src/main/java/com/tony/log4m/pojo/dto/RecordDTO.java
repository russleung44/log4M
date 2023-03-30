package com.tony.log4m.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    private Integer id;

    private Integer userId;

    private Integer accountId;

    @Schema(name = "交易类型 EXPENSE: 支出, INCOME: 收入")
    private String transactionType;

    @Schema(name = "金额")
    private BigDecimal amount;

    @Schema(name = "分类")
    private Integer categoryId;

    @Schema(name = "标签")
    private Integer tagId;

    @Schema(name = "备注")
    private String remark;

}
