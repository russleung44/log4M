package com.tony.log4m.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tony
 * @since 4/2/2025
 */
@Getter
@AllArgsConstructor
public enum MenuCommand {

    TODAY("today", "今日消费", "billCommand"),
    YESTERDAY("yesterday", "昨日消费", "billCommand"),
    THIS_MONTH("this_month", "本月消费", "billCommand"),
    LAST_MONTH("last_month", "上月消费", "billCommand"),
    RULES("rules", "规则列表", "ruleCommand");


    private final String command;
    private final String desc;
    private final String strategy;

    public static MenuCommand getByCommand(String command) {
        if (StrUtil.isBlank(command)) {
            throw new RuntimeException("指令不能为空");
        }

        for (MenuCommand value : MenuCommand.values()) {
            if (command.equals(value.getCommand())) {
                return value;
            }
        }
        throw new RuntimeException("不支持的指令");
    }
}
