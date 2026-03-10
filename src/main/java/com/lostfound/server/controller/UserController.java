package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.User;
import com.lostfound.server.service.UserService;
import com.lostfound.server.util.FileUploadUtil;
import com.lostfound.server.util.JwtUtil;
import com.lostfound.server.util.PageResult;
import com.lostfound.server.util.Result;
import com.lostfound.server.util.UserServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 功能：处理用户相关的所有HTTP请求，包括登录、注册、权限校验等
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;       // 用户服务层（数据库操作）
    
    @Autowired
    private JwtUtil jwtUtil;               // JWT工具类（生成/解析Token）
    
    @Autowired
    private UserServiceHelper userServiceHelper; // 用户服务辅助类（业务逻辑封装）
    
    @Autowired
    private FileUploadUtil fileUploadUtil; // 文件上传工具类

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
        String token = jwtUtil.generateToken(loginUser);
        
        // 5. 组装返回数据（用户信息脱敏）
        Map<String, Object> data = new HashMap<>();
        data.put("user", userServiceHelper.sanitizeUser(loginUser)); // 移除敏感字段
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
        String token = jwtUtil.generateToken(user);
        
        // 6. 组装返回数据（用户信息脱敏）
        Map<String, Object> data = new HashMap<>();
        data.put("user", userServiceHelper.sanitizeUser(user)); // 移除敏感字段
        data.put("token", token);
        
        return Result.success(data);
    }

    /**
     * 获取当前用户信息（Session方式）
     * @param session HTTP会话
     * @return 返回脱敏后的用户信息
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            log.warn("尝试获取未登录用户的Profile");
            return Result.error("请先登录");
        }
        return Result.success(userServiceHelper.sanitizeUser(user));
    }

    /**
     * 检查用户登录状态（兼容JWT和Session）
     * @param request HTTP请求对象
     * @return 返回当前用户信息
     */
    @GetMapping("/check")
    public Result<User> check(HttpServletRequest request) {
        // 1. 优先检查JWT Token
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(jwtToken);
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    return Result.success(userServiceHelper.sanitizeUser(user));
                }
            } catch (Exception e) {
                log.error("JWT解析失败: {}", e.getMessage());
            }
        }
        
        // 2. 回退检查Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success(userServiceHelper.sanitizeUser(user));
        }
        
        log.warn("未登录用户尝试访问受保护资源");
        return Result.error("未登录");
    }

    /**
     * 获取当前用户信息
     * 
     * 该接口用于获取当前登录用户的详细信息，包括用户ID、用户名、邮箱、电话等基本信息。
     * 该接口需要JWT认证，只有提供有效令牌的用户才能访问自己的信息。
     * 
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 包含用户详细信息的响应对象
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        // 1. 优先检查JWT Token
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(jwtToken);
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    return Result.success(userServiceHelper.sanitizeUser(user));
                }
            } catch (Exception e) {
                log.error("JWT解析失败: {}", e.getMessage());
            }
        }
        
        // 2. 回退检查Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success(userServiceHelper.sanitizeUser(user));
        }
        
        log.warn("未登录用户尝试访问/me接口");
        return Result.error("用户未登录");
    }

    /**
     * 用户退出登录
     * @param session HTTP会话
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
            log.info("用户已退出登录");
        }
        return Result.success("退出登录成功");
    }

    /**
     * 分页查询用户列表
     * @param pageNum 当前页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param username 用户名筛选（可选）
     * @param role 角色筛选（可选）
     * @param status 状态筛选（可选）
     * @return 分页数据
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        // 1. 执行分页查询
        Page<User> page = userService.getUserPage(pageNum, pageSize, username, role, status);
        
        // 2. 对每条数据脱敏处理
        page.getRecords().forEach(userServiceHelper::sanitizeUser);
        
        // 3. 转换为自定义分页响应格式
        PageResult<User> pageResult = PageResult.of(page);
        
        log.info("分页查询用户: 页码={}, 条数={}", pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 忘记密码接口
     * @param request 包含email和newPassword的Map
     * @return 重置结果
     */
    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody Map<String, String> request) {
        // 1. 参数提取与校验
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        if (email == null || email.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        // 2. 检查邮箱是否存在
        User user = userService.getUserByEmail(email.trim());
        if (user == null) {
            return Result.error("该邮箱尚未注册");
        }
        
        // 3. 执行密码重置
        boolean success = userService.resetPassword(email.trim(), newPassword);
        return success ? 
            Result.success("密码重置成功") : 
            Result.error("密码重置失败");
    }
    
    /**
     * 修改用户名接口
     * @param request 包含userId和newUsername的Map
     * @return 修改结果
     */
    @PostMapping("/update-username")
    public Result<String> updateUsername(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        // 1. 获取当前用户信息
        User currentUser = getCurrentUserFromRequest(httpRequest);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 2. 参数提取与校验
        String newUsername = (String) request.get("newUsername");
        Long userId = currentUser.getId(); // 使用当前登录用户的ID
        
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return Result.error("新用户名不能为空");
        }
        
        if (newUsername.length() < 3 || newUsername.length() > 20) {
            return Result.error("用户名长度应在3到20个字符之间");
        }
        
        // 3. 检查新用户名是否已被其他用户使用
        if (userService.isUsernameExists(newUsername.trim(), userId)) {
            return Result.error("该用户名已被使用");
        }
        
        // 4. 获取用户信息并更新用户名
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        String oldUsername = user.getUsername();
        user.setUsername(newUsername.trim());
        
        // 5. 执行更新
        boolean success = userService.updateById(user);
        
        if (success) {
            log.info("用户名修改成功，用户ID: {}, 原用户名: {}, 新用户名: {}", userId, oldUsername, newUsername);
            return Result.success("用户名修改成功");
        } else {
            log.error("用户名修改失败，用户ID: {}", userId);
            return Result.error("用户名修改失败");
        }
    }
    
    /**
     * 修改密码接口
     * @param request 包含oldPassword和newPassword的Map
     * @return 修改结果
     */
    @PostMapping("/update-password")
    public Result<String> updatePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        // 1. 获取当前用户信息
        User currentUser = getCurrentUserFromRequest(httpRequest);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 2. 参数提取与校验
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        Long userId = currentUser.getId(); // 使用当前登录用户的ID
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return Result.error("原密码不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        // 3. 执行密码修改
        boolean success = userService.changePassword(userId, oldPassword, newPassword);
        
        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.error("密码修改失败，请检查原密码是否正确");
        }
    }
    
    /**
     * 上传用户头像
     * 
     * @param file 上传的头像文件
     * @param httpRequest HTTP请求对象
     * @return 头像URL
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest httpRequest) {
        // 1. 检查用户是否登录
        User currentUser = getCurrentUserFromRequest(httpRequest);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 2. 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error("请选择要上传的头像文件");
        }
        
        // 3. 上传头像文件
        try {
            String avatarUrl = fileUploadUtil.uploadFile(file);
            return Result.success(avatarUrl);
        } catch (IOException e) {
            log.error("头像上传失败: {}", e.getMessage());
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新当前用户信息
     * @param userData 包含用户更新信息的Map
     * @param httpRequest HTTP请求对象
     * @return 更新结果
     */
    @PutMapping("/me")
    public Result<String> updateUser(@RequestBody Map<String, Object> userData, HttpServletRequest httpRequest) {
        // 1. 获取当前用户信息
        User currentUser = getCurrentUserFromRequest(httpRequest);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        Long userId = currentUser.getId();
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 2. 更新用户信息
        boolean updated = false;
        
        // 更新用户名
        if (userData.containsKey("username")) {
            String newUsername = (String) userData.get("username");
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                if (newUsername.length() < 3 || newUsername.length() > 20) {
                    return Result.error("用户名长度应在3到20个字符之间");
                }
                
                if (userService.isUsernameExists(newUsername.trim(), userId)) {
                    return Result.error("该用户名已被使用");
                }
                
                user.setUsername(newUsername.trim());
                updated = true;
            }
        }
        
        // 更新邮箱
        if (userData.containsKey("email")) {
            String newEmail = (String) userData.get("email");
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                // 简单的邮箱格式验证
                if (!newEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    return Result.error("邮箱格式不正确");
                }
                
                user.setEmail(newEmail.trim());
                updated = true;
            }
        }
        
        // 更新手机号
        if (userData.containsKey("phone")) {
            String newPhone = (String) userData.get("phone");
            if (newPhone != null && !newPhone.trim().isEmpty()) {
                // 简单的手机号格式验证
                if (!newPhone.matches("^1[3-9]\\d{9}$")) {
                    return Result.error("手机号格式不正确");
                }
                
                user.setPhone(newPhone.trim());
                updated = true;
            }
        }
        
        // 更新真实姓名
        // 注意：User实体类中没有realName字段，所以暂时注释掉
        // 如果需要添加真实姓名功能，请先在User实体类中添加realName字段
        /*
        if (userData.containsKey("realName")) {
            String newRealName = (String) userData.get("realName");
            if (newRealName != null) {
                user.setRealName(newRealName.trim());
                updated = true;
            }
        }
        */
        
        // 3. 执行更新
        if (updated) {
            boolean success = userService.updateById(user);
            if (success) {
                log.info("用户信息更新成功，用户ID: {}", userId);
                return Result.success("个人信息更新成功");
            } else {
                log.error("用户信息更新失败，用户ID: {}", userId);
                return Result.error("个人信息更新失败");
            }
        } else {
            return Result.success("没有需要更新的信息");
        }
    }
    
    /**
     * 从请求中获取当前用户信息
     * @param request HTTP请求对象
     * @return 当前用户信息
     */
    private User getCurrentUserFromRequest(HttpServletRequest request) {
        // 1. 优先检查JWT Token
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                // 优先使用用户ID查找用户
                Long userId = jwtUtil.getUserIdFromToken(jwtToken);
                if (userId != null) {
                    User user = userService.getById(userId);
                    if (user != null) {
                        return user;
                    }
                }
                
                // 如果无法通过用户ID获取，回退到用户名
                String username = jwtUtil.getUsernameFromToken(jwtToken);
                User user = userService.getUserByUsername(username);
                if (user != null) {
                    return user;
                }
            } catch (Exception e) {
                log.error("JWT解析失败: {}", e.getMessage());
            }
        }
        
        // 2. 回退检查Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user;
        }
        
        return null;
    }
}