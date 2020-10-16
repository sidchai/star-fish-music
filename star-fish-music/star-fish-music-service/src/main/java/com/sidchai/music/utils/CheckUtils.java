package com.sidchai.music.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 * @author sidchai
 * @since 2020-05-31
 */
public class CheckUtils {

    /**
     * 校验邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }
}
