package com.tony.log4m.utils;

/**
 * @author TonyLeung
 * @since 2022/10/11
 */
public class CommonUtil {

    public static boolean isZero(Number number) {
        return number == null || number.intValue() == 0;
    }

    public static boolean isNotZero(Number number) {
        return !isZero(number);
    }
}
