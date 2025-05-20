package com.tony.log4m.bots.enums;

import cn.hutool.core.util.StrUtil;
import com.tony.log4m.exception.Log4mException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tony
 * @since 4/2/2025
 */
@Getter
@AllArgsConstructor
public enum Command {

    TODAY("today", "今日消费", "bill"),
    YESTERDAY("yesterday", "昨日消费", "bill"),
    THIS_MONTH_SUMMARY("this_month_summary", "本月总结", "bill"),
    LAST_MONTH_SUMMARY("last_month_summary", "上月总结", "bill"),
    THIS_MONTH("this_month", "本月记录", "bill"),
    LAST_MONTH("last_month", "上月记录", "bill"),
    RULES("rules", "规则列表", "rule"),
    RULE_ADD("rule_add", "添加规则", "rule", false),
    CATEGORIES("categories", "分类列表", "category"),
    CATEGORY_ADD("category_add", "添加分类", "category", false),
    CATEGORY_DEFAULT("category_default", "设置默认分类", "category"),
    HELP("help", "帮助", "system"),
    RESET("reset", "重置", "system"),
    EXPORT("export", "导出", "system"),

    ;


    private final String command;
    private final String desc;
    private final String strategy;
    private final boolean show;

    Command(String command, String desc, String strategy) {
        this.command = command;
        this.desc = desc;
        this.strategy = strategy;
        this.show = true;
    }

    public static Command getByCommand(String command) {
        if (StrUtil.isBlank(command)) {
            throw new Log4mException("指令不能为空");
        }

        for (Command value : Command.values()) {
            if (command.equals(value.getCommand())) {
                return value;
            }
        }
        throw new Log4mException("不支持的指令");
    }
}
