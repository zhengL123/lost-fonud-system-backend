package com.lostfound.server.util;

import com.lostfound.server.entity.User;
import com.lostfound.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户服务工具类
 */
@Component
public class UserServiceHelper {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 清理用户敏感信息
     */
    public User sanitizeUser(User user) {
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 验证用户登录信息
     */
    public Result validateLogin(User user) {
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }
        
        User loginUser = userService.getUserByUsername(user.getUsername());
        if (loginUser == null) {
            return Result.error("用户不存在");
        }
        
        // 使用BCrypt验证密码
        if (!passwordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            return Result.error("密码错误");
        }
        
        // 检查用户状态
        if (!"active".equalsIgnoreCase(loginUser.getStatus()) && !"ACTIVE".equalsIgnoreCase(loginUser.getStatus())) {
            return Result.error("账户已被禁用");
        }
        
        return Result.success(loginUser);
    }
    
    /**
     * 验证用户注册信息
     */
    public Result validateRegistration(User user) {
        // 参数验证
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }
        
        // 检查用户名是否已存在（使用注册专用方法，检查所有状态的用户）
        if (userService.getUserByUsernameForRegistration(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 检查邮箱是否已存在（使用注册专用方法，检查所有状态的用户）
        if (StringUtils.hasText(user.getEmail()) && userService.getUserByEmailForRegistration(user.getEmail()) != null) {
            return Result.error("邮箱已被注册");
        }
        
        // 检查手机号是否已存在（使用注册专用方法，检查所有状态的用户）
        if (StringUtils.hasText(user.getPhone()) && userService.getUserByPhoneForRegistration(user.getPhone()) != null) {
            return Result.error("手机号已存在");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 处理用户注册
     */
    public void processRegistration(User user) {
        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getStatus() == null) {
            user.setStatus("ACTIVE");
        }
    }
    
    /**
     * 检查字符串是否为空
     */
    public boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串是否不为空
     */
    public boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }
}