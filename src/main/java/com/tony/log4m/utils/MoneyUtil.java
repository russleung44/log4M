package com.tony.log4m.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TonyLeung
 * @since 2022/10/12
 */
public class MoneyUtil {
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
            return matcher.group();
        }
        return null;
    }
}
