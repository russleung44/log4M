package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TonyLeung
 * @since 2023/7/10
 */
@Getter
@AllArgsConstructor
public enum AccountType {

    CASH("现金账户"),
    FINANCE("金融账户"),
    VIRTUALLY("虚拟账户"),
    CREDIT("信用账户"),
    LIABILITIES("负债账户"),
    DEBT("债权账户"),
    INVEST("投资账户"),
    INSURE("保险账户"),

    ;

    private final String desc;

    @Override
    public String toString() {
        return this.name() + ":" + this.desc;
    }


    public boolean isCredit() {
        return this == CREDIT || this == LIABILITIES;
    }

}
