package com.tony.log4m.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Tony
 * @since 2022-09-23 15:13:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AccountDTO {

    @NotNull(message = "id不能为空")
    private Integer id;

    private String accountName;

    private BigDecimal balance;

    private BigDecimal consume;

    private BigDecimal income;

    private BigDecimal consumeLimit;

    private Integer sort;

    private Integer status;

}
