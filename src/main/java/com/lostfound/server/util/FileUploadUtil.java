package com.lostfound.server.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 *
 * 提供文件上传相关功能，包括：
 * 1. 单文件上传
 * 2. 多文件上传
 * 3. 文件删除
 * 4. 文件类型验证
 * 5. 文件大小限制
 *
 * @author 系统开发团队
 * @version 1.0
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.domain}")
    private String uploadDomain;

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/jpg", "image/png");

    /**
     * 最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传单个文件
     *
     * @param file 要上传的文件
     * @return 文件访问URL
     * @throws IOException 文件上传异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        validateFile(file);

        // 创建按日期分类的目录
        String datePath = createDatePath();
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String relativePath = datePath + "/" + fileName;
        String fullPath = uploadPath + "/" + relativePath;

        // 确保目录存在
        Path filePath = Paths.get(fullPath);
        Files.createDirectories(filePath.getParent());

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        log.info("文件上传成功: {}", relativePath);

        // 返回可访问的URL
        String fileUrl = uploadDomain + "/uploads/" + relativePath;
        log.info("文件上传成功，返回URL: {}", fileUrl);
        return fileUrl;
    }

    /**
     * 上传多个文件
     *
     * @param files 要上传的文件数组
     * @return 文件访问URL列表
     * @throws IOException 文件上传异常
     */
    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String fileUrl = uploadFile(file);
                fileUrls.add(fileUrl);
            }
        }

        return fileUrls;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace(uploadDomain + "/uploads/", "");
            String fullPath = uploadPath + "/" + relativePath;

            Path filePath = Paths.get(fullPath);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                log.info("文件删除成功: {}", relativePath);
            } else {
                log.warn("文件不存在或删除失败: {}", relativePath);
            }

            return deleted;
        } catch (IOException e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 验证文件
     *
     * @param file 要验证的文件
     * @throws IllegalArgumentException 文件验证失败异常
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("只支持 JPG 和 PNG 格式的图片");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }
    }

    /**
     * 创建按日期分类的目录路径
     *
     * @return 日期路径，格式如：2023/12/25
     */
    private String createDatePath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}