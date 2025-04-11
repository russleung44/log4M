package com.tony.log4m.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tony
 * @since 4/3/2025
 */
@Getter
@RequiredArgsConstructor
public enum Command {

    RULE("ruleCustomCommand"),
    TAG("tagCustomCommand"),
    CLEAR("userCustomCommand"),
    ;

    private final String strategy;


}
