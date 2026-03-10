package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.Announcement;
import com.lostfound.server.entity.User;
import com.lostfound.server.service.AnnouncementService;
import com.lostfound.server.util.JwtUtil;
import com.lostfound.server.util.Result;
import com.lostfound.server.util.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 系统公告控制器
 * 
 * 提供系统公告的管理功能，包括：
 * 1. 公告的创建、查询、更新、删除
 * 2. 公告的发布、置顶、过期管理
 * 3. 公告的分类查询和搜索
 * 4. 公告的浏览统计
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "系统公告管理", description = "系统公告相关接口")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final JwtUtil jwtUtil;

    /**
     * 创建公告
     * 
     * 创建新的系统公告，创建后状态为草稿(DRAFT)
     * 
     * @param announcement 公告信息对象
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return Result<String> 创建成功返回"创建成功"，失败返回错误信息
     */
    @PostMapping
    @Operation(summary = "创建公告", description = "创建新的系统公告，创建后状态为草稿")
    public Result<String> createAnnouncement(@RequestBody Announcement announcement, HttpServletRequest request) {
        // 获取当前用户ID并设置到公告对象中
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(jwtToken);
                // 这里需要注入UserService来获取用户信息
                // 暂时使用固定值1，实际应该从JWT中解析用户ID
                announcement.setPublisherId(1L);
            } catch (Exception e) {
                log.error("JWT解析失败: {}", e.getMessage());
                return Result.error("用户认证失败");
            }
        } else {
            // 如果没有JWT Token，尝试从Session中获取
            HttpSession session = request.getSession();
            Object userObj = session.getAttribute("user");
            if (userObj instanceof User) {
                User user = (User) userObj;
                announcement.setPublisherId(user.getId());
            } else {
                return Result.error("用户未登录");
            }
        }
        
        boolean saved = announcementService.save(announcement);
        return saved ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 分页查询公告列表
     * 
     * 分页查询系统公告列表，支持按状态、类型、标题进行筛选
     * 
     * @param pageNum 当前页码，默认1
     * @param pageSize 每页大小，默认10
     * @param status 公告状态（可选）
     * @param announcementType 公告类型（可选）
     * @param title 公告标题（可选，用于模糊搜索）
     * @return Result<PageResult<Announcement>> 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询公告", description = "分页查询系统公告列表，支持按状态、类型、标题进行筛选")
    public Result<PageResult<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String announcementType,
            @RequestParam(required = false) String title) {
        
        Page<Announcement> page = announcementService.getAnnouncementPage(pageNum, pageSize, status, announcementType, title);
        // 转换为自定义分页响应格式
        PageResult<Announcement> pageResult = PageResult.of(page);
        return Result.success(pageResult);
    }

    /**
     * 获取已发布的公告列表
     * 
     * 获取所有已发布的公告列表，按发布时间排序
     * 
     * @return Result<List<Announcement>> 公告列表
     */
    @GetMapping("/published")
    @Operation(summary = "获取已发布公告", description = "获取所有已发布的公告列表")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 按类型获取公告列表
     * 
     * 根据公告类型获取已发布的公告列表
     * 
     * @param announcementType 公告类型
     * @return Result<List<Announcement>> 指定类型的公告列表
     */
    @GetMapping("/type/{announcementType}")
    @Operation(summary = "按类型获取公告", description = "根据公告类型获取已发布的公告列表")
    public Result<List<Announcement>> getAnnouncementsByType(@PathVariable String announcementType) {
        List<Announcement> announcements = announcementService.getAnnouncementsByType(announcementType);
        return Result.success(announcements);
    }

    /**
     * 获取最新公告列表
     * 
     * 获取最新的已发布的公告列表
     * 
     * @param limit 限制数量，默认10
     * @return Result<List<Announcement>> 最新公告列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新公告", description = "获取最新的已发布的公告列表")
    public Result<List<Announcement>> getLatestAnnouncements(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Announcement> announcements = announcementService.getLatestAnnouncements(limit);
        return Result.success(announcements);
    }

    /**
     * 根据ID查询公告详情
     * 
     * 通过公告ID获取公告的详细信息，包括发布人信息
     * 同时增加公告的浏览次数
     * 
     * @param id 公告ID
     * @return Result<Announcement> 公告详情，不存在则返回错误信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询公告详情", description = "通过公告ID获取公告的详细信息，同时增加浏览次数")
    public Result<Announcement> getById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementWithPublisher(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        
        // 增加浏览次数
        announcementService.increaseViewCount(id);
        
        return Result.success(announcement);
    }

    /**
     * 更新公告信息
     * 
     * 更新指定ID的公告信息，支持修改标题、内容、类型等字段
     * 
     * @param id 要更新的公告ID
     * @param announcement 包含更新信息的公告对象
     * @return Result<String> 更新成功返回"更新成功"，失败返回错误信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新公告", description = "更新指定ID的公告信息")
    public Result<String> updateById(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean updated = announcementService.updateById(announcement);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除公告
     * 
     * 删除指定ID的公告
     * 
     * @param id 要删除的公告ID
     * @return Result<String> 删除成功返回"删除成功"，失败返回错误信息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告", description = "删除指定ID的公告")
    public Result<String> deleteById(@PathVariable Long id) {
        boolean removed = announcementService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 发布公告
     * 
     * 将草稿状态的公告发布，使其对用户可见
     * 
     * @param id 要发布的公告ID
     * @return Result<String> 发布成功返回"发布成功"，失败返回错误信息
     */
    @PutMapping("/{id}/publish")
    @Operation(summary = "发布公告", description = "将草稿状态的公告发布，使其对用户可见")
    public Result<String> publishAnnouncement(@PathVariable Long id) {
        boolean published = announcementService.publishAnnouncement(id);
        return published ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 取消发布公告
     * 
     * 将已发布的公告取消发布，使其对用户不可见
     * 
     * @param id 要取消发布的公告ID
     * @return Result<String> 取消发布成功返回"取消发布成功"，失败返回错误信息
     */
    @PutMapping("/{id}/unpublish")
    @Operation(summary = "取消发布公告", description = "将已发布的公告取消发布，使其对用户不可见")
    public Result<String> unpublishAnnouncement(@PathVariable Long id) {
        boolean unpublished = announcementService.unpublishAnnouncement(id);
        return unpublished ? Result.success("取消发布成功") : Result.error("取消发布失败");
    }

    /**
     * 搜索公告
     * 
     * 根据关键词搜索已发布的公告，支持标题和内容的模糊匹配
     * 
     * @param keyword 搜索关键词
     * @return Result<List<Announcement>> 匹配的公告列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索公告", description = "根据关键词搜索已发布的公告")
    public Result<List<Announcement>> searchAnnouncements(@RequestParam String keyword) {
        List<Announcement> announcements = announcementService.searchAnnouncements(keyword);
        return Result.success(announcements);
    }

    /**
     * 批量删除公告
     * 
     * 批量删除多个公告
     * 
     * @param ids 要删除的公告ID列表
     * @return Result<String> 删除成功返回"批量删除成功"，失败返回错误信息
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除公告", description = "批量删除多个公告")
    public Result<String> batchDeleteAnnouncements(@RequestBody List<Long> ids) {
        boolean deleted = announcementService.batchDeleteAnnouncements(ids);
        return deleted ? Result.success("批量删除成功") : Result.error("批量删除失败");
    }

    /**
     * 批量发布公告
     * 
     * 批量发布多个草稿状态的公告
     * 
     * @param ids 要发布的公告ID列表
     * @return Result<String> 发布成功返回"批量发布成功"，失败返回错误信息
     */
    @PutMapping("/batch/publish")
    @Operation(summary = "批量发布公告", description = "批量发布多个草稿状态的公告")
    public Result<String> batchPublishAnnouncements(@RequestBody List<Long> ids) {
        boolean published = announcementService.batchPublishAnnouncements(ids);
        return published ? Result.success("批量发布成功") : Result.error("批量发布失败");
    }

    /**
     * 批量取消发布公告
     * 
     * 批量取消发布多个已发布的公告
     * 
     * @param ids 要取消发布的公告ID列表
     * @return Result<String> 取消发布成功返回"批量取消发布成功"，失败返回错误信息
     */
    @PutMapping("/batch/unpublish")
    @Operation(summary = "批量取消发布公告", description = "批量取消发布多个已发布的公告")
    public Result<String> batchUnpublishAnnouncements(@RequestBody List<Long> ids) {
        boolean unpublished = announcementService.batchUnpublishAnnouncements(ids);
        return unpublished ? Result.success("批量取消发布成功") : Result.error("批量取消发布失败");
    }
}