package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TonyLeung
 * @since 2023/4/13
 */
@Getter
@AllArgsConstructor
public enum CurrencyEnum {
    CNY("CNY", "人民币"),
    USD("USD", "美元"),
    EUR("EUR", "欧元"),
    GBP("GBP", "英镑"),
    HKD("HKD", "港币"),
    MOP("MOP", "澳门币"),
    SGD("SGD", "新加坡币"),
    JPY("JPY", "日元"),
    TWD("TWD", "新台币"),
    KRW("KRW", "韩元"),
    THB("THB", "泰铢"),
    VND("VND", "越南盾"),
    IDR("IDR", "印尼盾"),
    PHP("PHP", "菲律宾比索"),
    INR("INR", "印度卢比"),
    MYR("MYR", "马来西亚林吉特"),
    AUD("AUD", "澳大利亚元"),
    CAD("CAD", "加拿大元"),
    NZD("NZD", "新西兰元"),
    CHF("CHF", "瑞士法郎"),
    SEK("SEK", "瑞典克朗"),
    NOK("NOK", "挪威克朗"),
    TRY("TRY", "土耳其里拉"),

    ;

    private final String code;
    private final String desc;

    @Override
    public String toString() {
        return this.name() + ":" + this.desc;
    }

}
