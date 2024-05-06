package com.tony.log4m.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tony.log4m.base.BaseEntity;
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

    private String accountName;

    private BigDecimal balance;

    private BigDecimal consume;

    private BigDecimal income;

    private BigDecimal consumeLimit;

    private Integer sort;

    private Integer status;

    private boolean isDefault;
}
