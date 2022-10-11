package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TonyLeung
 * @date 2022/10/10
 */
@Getter
@AllArgsConstructor
public enum TransactionType {

    EXPENSE("EXPENSE", "支出"),
    INCOME("INCOME", "收入");

    private final String type;
    private final String desc;

}
