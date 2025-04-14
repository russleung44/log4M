package com.tony.log4m.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tony
 * @since 4/14/2025
 */
@Getter
@AllArgsConstructor
public enum DateKeyword {

    TODAY(Arrays.asList("今天", "今日")),
    YESTERDAY(Arrays.asList("昨天", "昨日")),
    DAY_BEFORE_YESTERDAY(Arrays.asList("前天", "前日")),
    TOMORROW(Arrays.asList("明天", "明日"));

    private final List<String> keywords;

    public static DateKeyword fromInput(String input) {
        for (DateKeyword keyword : DateKeyword.values()) {
            if (keyword.matches(input)) {
                return keyword;
            }
        }
        return null;
    }

    public boolean matches(String input) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String[] getKeywords() {
        return keywords.toArray(new String[0]);
    }
}