package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tony
 * @since 4/2/2025
 */
@Getter
@AllArgsConstructor
public enum CommandType {

    TODAY("today", "今日消费"),
    YESTERDAY("yesterday", "昨日消费"),
    THIS_MONTH("this_month", "本月消费"),
    LAST_MONTH("last_month", "上月消费"),
    RULES("rules", "规则列表");;


    private final String code;
    private final String desc;

    public static CommandType getByCode(String replace) {
        for (CommandType value : CommandType.values()) {
            if (value.getCode().equals(replace)) {
                return value;
            }
        }
        throw new RuntimeException("不支持的指令");
    }
}
