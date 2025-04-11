package com.tony.log4m.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
import com.tony.log4m.enums.TransactionType;
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
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "rule", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Rule extends BaseEntity<Rule> {

    private String name;

    private Long accountId;

    private TransactionType transactionType;

    private Long categoryId;

    private Long tagId;

    private BigDecimal amount;

    private String keywords;

    private Integer sort;

    public Rule(String keyword, BigDecimal amount, TransactionType transactionType) {
        this.name = keyword;
        this.keywords = keyword;
        this.amount = amount;
        this.transactionType = transactionType;
    }
}
