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
 * 账户
 *
 * @author Tony
 * @since 2022-09-23 16:00:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "account", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {

    private Integer userId;

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

    @Schema(name = "0: no, 1: yes")
    private boolean isDefault;
}
