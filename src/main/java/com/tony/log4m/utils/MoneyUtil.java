package com.tony.log4m.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.tony.log4m.enums.DateKeyword;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TonyLeung
 * @since 2022/10/12
 */
public class MoneyUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 获取第一个金额
     *
     * @param text 文本
     * @return 金额, 可负数, 保留两位小数
     */
    public static String getAmount(String text) {
        Pattern integerPattern = Pattern.compile("-?\\d+(\\.\\d{1,2})?");
        Matcher matcher = integerPattern.matcher(text);
        if (matcher.find()) {
            text = StrUtil.replace(text, matcher.group(), "");
            return matcher.group();
        }
        throw new RuntimeException("金额解析失败");
    }


    public static Result getDate(String input) {
        LocalDate billDate = LocalDate.now();

        // 优先处理自然语言
        for (DateKeyword keyword : DateKeyword.values()) {
            if (keyword.matches(input)) {
                for (String key : keyword.getKeywords()) {
                    input = StrUtil.replace(input, key, "");
                }
                switch (keyword) {
                    case YESTERDAY -> billDate = billDate.minusDays(1);
                    case DAY_BEFORE_YESTERDAY -> billDate = billDate.minusDays(2);
                    case TOMORROW -> billDate = billDate.plusDays(1);
                }
            }
        }

        // 处理分割后的日期片段
        String[] parts = input.split("\\D+");
        for (String part : parts) {
            if (part.length() == 8) {
                try {
                    billDate = LocalDate.parse(part, DATE_FORMATTER);
                    input = StrUtil.replace(input, part, "");
                } catch (DateTimeParseException e) {
                    // 忽略无效格式，继续循环
                }
            } else if (part.length() == 4) {
                String fullDateStr = Year.now().getValue() + part;
                try {
                    billDate = LocalDate.parse(fullDateStr, DATE_FORMATTER);
                    input = StrUtil.replace(input, part, "");
                } catch (DateTimeParseException e) {
                    // 忽略无效格式，继续循环
                }
            }
        }

        // 如果没有找到匹配的日期，返回当前日期
        return new Result(input, billDate);
    }

    public static String getMonth(LocalDate billDate) {
        return LocalDateTimeUtil.format(billDate, "yyyyMM");
    }


    public record Result(String text, LocalDate date) {
    }

    ;

}
