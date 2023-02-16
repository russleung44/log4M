package com.tony.log4m.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户余额")
    private BigDecimal balance;

    @ApiModelProperty("消费总额")
    private BigDecimal consume;

    @ApiModelProperty("收入总额")
    private BigDecimal income;

    @ApiModelProperty("消费限制")
    private BigDecimal consumeLimit;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("0: inactive, 1: active")
    private Integer status;

}
