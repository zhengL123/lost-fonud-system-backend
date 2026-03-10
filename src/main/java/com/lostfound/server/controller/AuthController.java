package com.lostfound.server.controller;

import com.lostfound.server.entity.User;
import com.lostfound.server.entity.User.UserSimpleVO;
import com.lostfound.server.service.UserService;
import com.lostfound.server.util.JwtUtil;
import com.lostfound.server.util.Result;
import com.lostfound.server.util.UserServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 功能：处理用户认证相关的HTTP请求，包括登录、注册等
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;       // 用户服务层（数据库操作）
    
    @Autowired
    private JwtUtil jwtUtil;               // JWT工具类（生成/解析Token）
    
    @Autowired
    private UserServiceHelper userServiceHelper; // 用户服务辅助类（业务逻辑封装）

    /**
     * 用户登录接口
     * @param user 前端传入的登录信息（需包含username和password）
     * @param session HTTP会话对象（用于存储传统Session）
     * @return 返回用户信息和JWT Token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user, HttpSession session) {
        // 1. 验证登录信息（检查用户名密码格式、是否存在、是否匹配）
        Result<User> validationResult = userServiceHelper.validateLogin(user);
        if (validationResult.getCode() != 200) {
            log.warn("登录验证失败: {}", validationResult.getMessage());
            return Result.error(validationResult.getMessage());
        }
        
        // 2. 获取验证通过的用户数据
        User loginUser = validationResult.getData();
        
        // 3. 存储Session（兼容传统有状态认证）
        session.setAttribute("user", loginUser);
        log.info("用户登录成功: {}", loginUser.getUsername());
        
        // 4. 生成JWT Token（无状态认证）
        String token = jwtUtil.generateToken(
            loginUser.getUsername(), 
            loginUser.getRole()
        );
        
        // 5. 组装返回数据（用户信息脱敏）
        Map<String, Object> data = new HashMap<>();
        
        // 使用UserSimpleVO避免序列化问题
        UserSimpleVO userSimpleVO = new UserSimpleVO();
        BeanUtils.copyProperties(loginUser, userSimpleVO);
        data.put("user", userSimpleVO);
        data.put("token", token);
        
        return Result.success(data);
    }

    /**
     * 用户注册接口
     * @param user 前端传入的注册信息
     * @param session HTTP会话对象（用于存储传统Session）
     * @return 返回注册结果，包含用户信息和JWT Token
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user, HttpSession session) {
        // 1. 验证注册信息（用户名是否重复、密码强度等）
        Result<String> validationResult = userServiceHelper.validateRegistration(user);
        if (validationResult.getCode() != 200) {
            log.warn("注册验证失败: {}", validationResult.getMessage());
            return Result.error(validationResult.getMessage());
        }
        
        // 2. 处理注册逻辑（密码加密、设置默认角色等）
        userServiceHelper.processRegistration(user);
        
        // 3. 保存到数据库
        boolean success = userService.save(user);
        if (!success) {
            log.error("用户注册失败: {}", user.getUsername());
            return Result.error("注册失败");
        }
        
        log.info("用户注册成功: {}", user.getUsername());
        
        // 4. 注册成功后自动登录
        // 存储Session（兼容传统有状态认证）
        session.setAttribute("user", user);
        
        // 5. 生成JWT Token（无状态认证）
        String token = jwtUtil.generateToken(
            user.getUsername(), 
            user.getRole()
        );
        
        // 6. 组装返回数据（用户信息脱敏）
        Map<String, Object> data = new HashMap<>();
        
        // 使用UserSimpleVO避免序列化问题
        UserSimpleVO userSimpleVO = new UserSimpleVO();
        BeanUtils.copyProperties(user, userSimpleVO);
        data.put("user", userSimpleVO);
        data.put("token", token);
        
        return Result.success(data);
    }
}