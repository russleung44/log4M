package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TonyLeung
 * @since 2022/10/10
 */
@Getter
@AllArgsConstructor
public enum TransactionType {

    EXPENSE("支出", "bill"),
    INCOME("收入", "bill"),
    TRANSFER("转账", "bill"),
    LEND("借出", "debt"),
    BORROW("借入", "debt"),
    PAID_ON_BEHALF("代付", "debt"),
    REPAY("还债", "settlement"),
    COLLECT("收债", "settlement"),
    BALANCE_CHANGE("余额变更", "account"),

    ;

    private final String type;
    private final String desc;

    public String getPrefix() {
        return switch (this) {
            case EXPENSE -> "-";
            case INCOME -> "+";
            default -> "";
        };
    }
}
