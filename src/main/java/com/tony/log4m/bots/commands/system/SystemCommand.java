package com.tony.log4m.bots.commands.system;

import com.pengrad.telegrambot.request.SendMessage;
import com.tony.log4m.bots.enums.MenuCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Tony
 * @since 4/11/2025
 */
@Component
@RequiredArgsConstructor
public class SystemCommand implements SystemCommandStrategy {


    @Override
    public SendMessage execute(MenuCommand menuCommand, Long chatId) {
        switch (menuCommand) {
            case HELP -> {
                String help = """
                        新建规则: @RULE@#{规则名称}-#{金额}-#{1:支付，0:收入}
                        规则示例: @RULE@烧鸭肶-18-1
                        """;
                return new SendMessage(chatId, help);
            }
            default -> {
                return new SendMessage(chatId, "未识别的命令");
            }
        }
    }


}
