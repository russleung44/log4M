package com.tony.log4m.pojo.dto;

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
public class RuleDTO {

    private Integer id;

    private String name;

    private Integer userId;

    private Integer accountId;

    private String transactionType;

    private Integer categoryId;

    private Integer tagId;

    private BigDecimal amount;

    private String keywords;

    private Integer sort;

}
