package com.tony.log4m.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(name = "账户名称")
    private String accountName;

    @Schema(name = "账户余额")
    private BigDecimal balance;

    @Schema(name = "消费总额")
    private BigDecimal consume;

    @Schema(name = "收入总额")
    private BigDecimal income;

    @Schema(name = "消费限制")
    private BigDecimal consumeLimit;

    @Schema(name = "排序")
    private Integer sort;

    @Schema(name = "0: inactive, 1: active")
    private Integer status;

}
