package com.example.demo.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @Author: YT
 * @Description: 密码操作
 * @DateTime: 2022/9/27$ - 11:22
 */
public class PasswordUtil {
    public static String encrypt(String password) {
        String salt = IdUtil.simpleUUID();
        String finalPassword = salt + "$" + SecureUtil.md5(salt + password);
        return finalPassword;
    }
    public static boolean comparePassword (String password, String securePassword) {
        String[] temp = securePassword.split("\\$");
        String salt = temp[0];
        String md5Password = temp[1];
        return (salt + "$" + SecureUtil.md5(salt + password)).equals(securePassword);
    }
}
