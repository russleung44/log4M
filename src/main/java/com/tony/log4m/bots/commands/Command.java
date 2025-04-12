package com.tony.log4m.bots.commands;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class Command implements CommandStrategy {


    @Override
    public SendMessage execute(com.tony.log4m.bots.enums.Command command, String param, Long chatId) {
        switch (command) {
            case HELP -> {
                String help = """
                        新建规则: @RULE@#{规则名称}-#{金额}-#{1:支付，0:收入}
                        规则示例: @RULE@烧鸭肶-18-1
                        """;
                return new SendMessage(chatId, help);
            }

            case RESET -> {
                return new SendMessage(chatId, "重置成功");
            }

            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }


}
