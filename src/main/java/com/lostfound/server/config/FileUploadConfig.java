package com.lostfound.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 文件上传配置类
 * 
 * 提供文件上传相关配置，包括：
 * 1. 静态资源映射配置
 * 2. 上传目录初始化
 * 3. 文件访问路径配置
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Slf4j
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.domain}")
    private String uploadDomain;

    /**
     * 配置静态资源映射
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传目录的静态资源映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
        
        // 配置avatars目录的静态资源映射，确保头像图片可以正确访问
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + uploadPath + "/avatars/");
        
        log.info("文件上传静态资源映射配置完成: /uploads/** -> file:{}", uploadPath);
        log.info("头像静态资源映射配置完成: /avatars/** -> file:{}/avatars/", uploadPath);
    }

    /**
     * 应用启动时初始化上传目录
     * 
     * @return ApplicationRunner
     */
    @Bean
    public ApplicationRunner initUploadDirectory() {
        return args -> {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    log.info("文件上传目录创建成功: {}", uploadPath);
                } else {
                    log.error("文件上传目录创建失败: {}", uploadPath);
                }
            } else {
                log.info("文件上传目录已存在: {}", uploadPath);
            }
            
            log.info("文件访问域名: {}", uploadDomain);
        };
    }
}