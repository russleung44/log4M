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

    @Schema(name = "标题")
    private String title;

    @Schema(name = "日期")
    private String date;

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
