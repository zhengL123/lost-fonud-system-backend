package com.lostfound.server.controller;

import com.lostfound.server.util.FileUploadUtil;
import com.lostfound.server.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件上传控制器
 * 
 * 提供文件上传相关接口，包括：
 * 1. 单文件上传
 * 2. 多文件上传
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadUtil fileUploadUtil;

    /**
     * 上传单个文件
     * 
     * @param file 要上传的文件
     * @return Result<String> 包含文件访问URL
     */
    @PostMapping("/single")
    public Result<String> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileUploadUtil.uploadFile(file);
            return Result.success(fileUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传多个文件
     * 
     * @param files 要上传的文件列表
     * @return Result<List<String>> 包含文件访问URL列表
     */
    @PostMapping("/multiple")
    public Result<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileUrls = fileUploadUtil.uploadFiles(files);
            return Result.success(fileUrls);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}