package com.tony.log4m.bots;

import com.tony.log4m.bots.enums.CustomCommand;

/**
 * 命令解析器
 *
 * @author Tony
 * @since 4/11/2025
 */
public class CommandParser {

    private final String text;

    public CommandParser(String text) {
        this.text = text;
    }

    public CustomCommand getCommand() {
        String cmd = text.substring(1, text.lastIndexOf("/")).toUpperCase();
        return CustomCommand.valueOf(cmd);
    }

    public String[] getParams() {
        return text.substring(text.lastIndexOf("/") + 1).split("-");
    }

}