package com.lostfound.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
//跨域
@Configuration
public class CorsConfig {
    
    @Value("${frontend.url}")
    private String frontendUrl;
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端地址
        config.addAllowedOrigin(frontendUrl);
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带认证信息
        config.setAllowCredentials(true);
        // 暴露响应头，允许前端访问自定义头
        config.addExposedHeader("Content-Disposition");
        config.addExposedHeader("Content-Type");
        // 允许暴露所有响应头，解决跨域访问静态资源的问题
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}