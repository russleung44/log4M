package com.tony.log4m.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.tony.log4m.enums.DateKeyword;
import com.tony.log4m.exception.Log4mException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TonyLeung
 * @since 2022/10/12
 */
public class MoneyUtil {

    private static final String[] DATE_PATTERNS = {
            "\\d{4}-\\d{2}-\\d{2}",     // yyyy-MM-dd
            "\\d{4}/\\d{2}/\\d{2}",     // yyyy/MM/dd
            "\\d{2}-\\d{2}-\\d{4}",     // dd-MM-yyyy
            "\\d{2}/\\d{2}/\\d{4}",     // dd/MM/yyyy
            "\\d{8}"                    // yyyyMMdd
    };

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd")   // 新增格式
    };

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
        throw new Log4mException("金额解析失败");
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

        for (int i = 0; i < DATE_PATTERNS.length; i++) {
            Pattern pattern = Pattern.compile(DATE_PATTERNS[i]);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                String dateStr = matcher.group();
                try {
                    billDate = LocalDate.parse(dateStr, FORMATTERS[i]);
                    input = StrUtil.replace(input, dateStr, "");
                } catch (DateTimeParseException e) {
                    // 格式匹配但解析失败时忽略
                }
            }
        }

        // 如果没有找到匹配的日期，返回当前日期
        return new Result(input, billDate);
    }


    public static String getMonth(LocalDate billDate) {
        return LocalDateTimeUtil.format(billDate, "yyyyMM");
    }

    public static String formatBigDecimal(BigDecimal money) {
        return money.stripTrailingZeros().toPlainString();
    }


    public record Result(String text, LocalDate date) {
    }

    ;

}
