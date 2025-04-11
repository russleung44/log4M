package com.tony.log4m.bots.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tony
 * @since 4/3/2025
 */
@Getter
@RequiredArgsConstructor
public enum CustomCommand {

    RULE("ruleCustomCommand"),
    TAG("tagCustomCommand"),
    CLEAR("userCustomCommand"),
    ;

    private final String strategy;


}
