package com.tony.log4m.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatVO {
    private Integer totalAccount;
    private BigDecimal totalBalance;
    private Integer activeAccount;
    private Integer frozenAccount;
}
