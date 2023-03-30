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

    @Schema(name = "规则名称")
    private String name;

    private Integer userId;

    private Integer accountId;

    @Schema(name = "交易类型 EXPENSE: 支出, INCOME: 收入")
    private String transactionType;

    @Schema(name = "分类")
    private Integer categoryId;

    @Schema(name = "标签")
    private Integer tagId;

    @Schema(name = "默认金额")
    private BigDecimal amount;

    @Schema(name = "关键字")
    private String keywords;

    @Schema(name = "排序")
    private Integer sort;

}
