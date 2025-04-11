package com.tony.log4m.bots;

import com.tony.log4m.enums.Command;

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

    public Command getCommand() {
        String cmd = text.substring(1, text.lastIndexOf("@")).toUpperCase();
        return Command.valueOf(cmd);
    }

    public String[] getParams() {
        return text.substring(text.lastIndexOf("@") + 1).split("-");
    }

}