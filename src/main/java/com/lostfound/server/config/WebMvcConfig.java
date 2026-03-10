package com.lostfound.server.config;

import com.lostfound.server.interceptor.AdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Web MVC配置类
 * 
 * 该类负责配置Spring MVC的核心组件，包括：
 * 1. 拦截器注册 - 配置管理员权限拦截器
 * 2. 消息转换器 - 设置HTTP响应编码为UTF-8，解决中文乱码问题
 * 
 * Spring MVC的配置类
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 管理员权限拦截器
     * 负责验证用户是否具有管理员权限，保护管理员专用接口
     */
    @Autowired
    private AdminInterceptor adminInterceptor;

    /**
     * 注册拦截器
     * 
     * 配置管理员权限拦截器的拦截路径和排除路径
     * 
     * @param registry 拦截器注册器，用于添加和配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理员权限拦截器配置
        // 拦截需要管理员权限的接口，确保只有管理员可以访问
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(

                        "/api/admin/**",           // 管理员专用接口
                        "/api/admin/items",         // 管理员物品管理
                        "/api/users/delete",        // 删除用户
                        "/api/users/status",        // 修改用户状态
                        "/api/claim-records/process", // 处理认领记录
                        "/api/categories/delete",   // 删除分类
                        
                        // 公告管理相关API - 需要管理员权限
                        "/api/announcements",       // 创建公告
                        "/api/announcements/*",     // 公告的修改、删除、发布等操作
                        "/api/announcements/batch/*" // 批量操作公告
                )
                .excludePathPatterns(
                        "/api/admin/statistics",    // 管理员统计接口（不需要特殊权限）
                        "/api/admin/cache/**",      // 缓存管理接口（不需要特殊权限）
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        // 公告查询相关API - 公开访问，不需要管理员权限
                        "/api/announcements/page",  // 分页查询公告
                        "/api/announcements/published", // 查询已发布公告
                        "/api/announcements/top",   // 查询置顶公告
                        "/api/announcements/type/*", // 按类型查询公告
                        "/api/announcements/latest", // 查询最新公告
                        "/api/announcements/search" // 搜索公告
                );
    }

    /**
     * 配置HTTP消息转换器
     * 
     * 添加字符串消息转换器，设置响应编码为UTF-8
     * 这可以解决中文响应乱码问题，确保前端正确显示中文内容
     * 
     * @param converters 消息转换器列表，Spring MVC使用这些转换器处理HTTP请求和响应
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建字符串消息转换器，指定UTF-8编码
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        // 将转换器添加到转换器列表中
        converters.add(stringConverter);
    }
}