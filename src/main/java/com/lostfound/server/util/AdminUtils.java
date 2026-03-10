package com.lostfound.server.util;

import com.lostfound.server.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * 管理员工具类
 */
public class AdminUtils {
    
    /**
     * 清理用户敏感信息
     */
    public static User sanitizeUser(User user) {
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 检查字符串是否为空或空白
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串是否不为空且不为空白
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }
    
    /**
     * 加密密码
     */
    public static String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    
    /**
     * 获取默认密码
     */
    public static String getDefaultPassword() {
        return "123456";
    }
}