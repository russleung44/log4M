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
public class RecordDTO {

    private Integer id;

    private Integer userId;

    private Integer accountId;

    private String transactionType;

    private BigDecimal amount;

    private Integer categoryId;

    private Integer tagId;

    private String remark;

}
