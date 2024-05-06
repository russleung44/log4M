package com.tony.log4m.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import com.tony.log4m.enums.TransactionType;
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

    private String name;

    private Integer userId;

    private Integer accountId;

    private TransactionType transactionType;

    private Integer categoryId;

    private Integer tagId;

    private BigDecimal amount;

    private String keywords;

    private Integer sort;

}
