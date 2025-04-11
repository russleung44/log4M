package com.tony.log4m.external.tutu;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Tony
 * @since 4/6/2025
 */
@Data
public class TutuData {

    /**
     * 支出发生的日期。
     */
    private String date;

    /**
     * 支出类型，例如“支出”或“收入”。
     */
    private String type;

    /**
     * 支出类别，例如“餐饮”、“交通”、“订阅服务”等。
     */
    private String category;

    /**
     * 支出子类别（可选）。
     */
    private String subCategory;

    /**
     * 支出的货币类型，例如“CNY”。
     */
    private String currency;

    /**
     * 支出的金额。
     */
    private BigDecimal amount;

    /**
     * 关于支出的附加备注或说明。
     */
    private String note;

    /**
     * 关联的第一个账户。
     */
    private String account1;

    /**
     * 关联的第二个账户（可选）。
     */
    private String account2;

    /**
     * 记录支出的账本。
     */
    private String ledger;

    /**
     * 关联的标签（可选）。
     */
    private String tags;

}
