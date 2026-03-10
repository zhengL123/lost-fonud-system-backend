package com.lostfound.server.interceptor;

import com.lostfound.server.entity.User;
import com.lostfound.server.service.UserService;
import com.lostfound.server.util.Result;
import com.lostfound.server.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 管理员权限拦截器
 * 
 * 该拦截器用于验证用户是否具有管理员权限，确保只有管理员用户才能访问需要管理员权限的API。
 * 拦截器会首先尝试从JWT令牌中获取用户角色信息，如果JWT验证失败，则会尝试从Session中获取用户信息。
 * 
 * @author 系统生成
 * @version 1.0
 * @since 2023-01-01
 */
@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    /**
     * JSON序列化工具，用于构建错误响应
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 用户服务，用于获取用户信息
     */
    @Autowired
    @Lazy
    private UserService userService;
    
    /**
     * JWT工具类，用于令牌的验证和解析
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 需要管理员权限的路径
     * 
     * 这些路径需要用户具有管理员角色才能访问：
     * - 用户管理：删除用户、修改用户状态等
     * - 认领记录处理：处理失物认领申请
     * - 分类管理：删除分类等
     * - 管理员专用接口：管理员相关的API
     */
    private static final List<String> ADMIN_PATHS = Arrays.asList(
            "/api/users/delete",
            "/api/users/status",
            "/api/claim-records/process",
            "/api/categories/delete",
            "/api/admin/",
            "/api/admin/items"
    );
    
    /**
     * 不需要拦截的管理员路径
     * 
     * 这些路径虽然以"/api/admin"开头，但不需要管理员权限验证：
     * - 统计数据接口：可能对普通用户开放
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/admin/statistics"
    );

    /**
     * 请求处理前的拦截方法
     * 
     * 该方法在控制器方法执行前被调用，用于验证用户是否具有管理员权限。
     * 验证流程：
     * 1. 检查请求路径是否需要管理员权限
     * 2. 尝试从JWT令牌中获取用户角色信息
     * 3. 如果JWT验证失败，尝试从Session中获取用户信息
     * 4. 验证用户角色是否为管理员
     * 
     * @param request 当前HTTP请求对象
     * @param response 当前HTTP响应对象
     * @param handler 被调用的处理器方法
     * @return true表示继续处理请求，false表示中断请求处理
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求URI
        String requestURI = request.getRequestURI();

        // 记录管理员权限检查日志
        log.info("管理员权限检查: {}", requestURI);

        // 检查是否需要管理员权限
        if (!requiresAdminPermission(requestURI)) {
            return true;
        }

        // 初始化用户信息变量
        String username = null;
        String jwtToken = null;
        String role = null;

        // 首先尝试从JWT token中获取用户信息
        final String requestTokenHeader = request.getHeader("Authorization");
        
        // JWT Token格式为 "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // 去掉Bearer前缀，获取纯令牌字符串
            jwtToken = requestTokenHeader.substring(7);
            try {
                // 从令牌中提取用户名和角色
                username = jwtUtil.getUsernameFromToken(jwtToken);
                role = jwtUtil.getRoleFromToken(jwtToken);
            } catch (Exception e) {
                // 记录无法获取JWT Token中用户信息的错误
                log.error("无法获取JWT Token中的用户信息: {}", e.getMessage());
            }
        }

        // 如果JWT token验证成功
        if (username != null && role != null && jwtUtil.validateToken(jwtToken, username)) {
            // 检查用户角色是否为管理员
            if (!"ADMIN".equalsIgnoreCase(role)) {
                // 如果不是管理员，返回403禁止访问错误
                sendErrorResponse(response, Result.error(403, "需要管理员权限"));
                return false;
            }
            
            // 记录管理员操作日志
            log.info("管理员操作: 用户名={}, 路径={}", username, requestURI);
            return true;
        }

        // 如果JWT token验证失败，尝试从Session中获取用户信息（兼容旧的Session验证方式）
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 如果Session中也没有用户信息，返回401未授权错误
        if (user == null) {
            sendErrorResponse(response, Result.error(401, "用户信息不存在"));
            return false;
        }

        // 检查用户角色是否为管理员
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            // 如果不是管理员，返回403禁止访问错误
            sendErrorResponse(response, Result.error(403, "需要管理员权限"));
            return false;
        }

        // 记录管理员操作日志
        log.info("管理员操作: 用户ID={}, 路径={}", user.getId(), requestURI);
        return true;
    }

    /**
     * 检查路径是否需要管理员权限
     * 
     * 该方法用于判断当前请求的URI是否需要管理员权限。
     * 首先检查是否在排除列表中，如果在排除列表中则不需要管理员权限。
     * 然后检查是否在管理员路径列表中，如果在则需要管理员权限。
     * 
     * @param requestURI 当前请求的URI
     * @return true表示需要管理员权限，false表示不需要
     */
    private boolean requiresAdminPermission(String requestURI) {
        // 检查是否在排除列表中
        boolean isExcluded = EXCLUDE_PATHS.stream().anyMatch(excludePath ->
                requestURI.equals(excludePath)
        );
        
        // 如果在排除列表中，则不需要管理员权限
        if (isExcluded) {
            return false;
        }
        
        // 检查是否需要管理员权限
        return ADMIN_PATHS.stream().anyMatch(adminPath ->
                requestURI.startsWith(adminPath) ||
                        requestURI.contains(adminPath)
        );
    }

    /**
     * 发送错误响应
     * 
     * 当用户没有管理员权限时，调用此方法构建并返回错误响应。
     * 响应格式为JSON，包含错误码、错误消息和数据字段。
     * 
     * @param response HTTP响应对象
     * @param result 错误结果对象
     * @throws Exception 写入响应时可能抛出的异常
     */
    private void sendErrorResponse(HttpServletResponse response, Result<?> result) throws Exception {
        // 设置响应状态码和内容类型
        response.setStatus(result.getCode());
        response.setContentType("application/json;charset=utf-8");
        
        // 写入响应内容
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}