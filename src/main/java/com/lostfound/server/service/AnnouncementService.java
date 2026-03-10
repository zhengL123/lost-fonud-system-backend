package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.Announcement;

import java.util.List;

/**
 * 系统公告Service接口
 * 定义系统公告的业务逻辑操作
 * 继承MyBatis-Plus的IService，提供基本的CRUD操作
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告列表
     * 
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param status 公告状态（可选）
     * @param announcementType 公告类型（可选）
     * @param title 公告标题（可选，用于模糊搜索）
     * @return 分页结果
     */
    Page<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize, String status, String announcementType, String title);

    /**
     * 获取已发布的公告列表
     * 
     * @return 公告列表
     */
    List<Announcement> getPublishedAnnouncements();

    /**
     * 按类型获取公告列表
     * 
     * @param announcementType 公告类型
     * @return 指定类型的公告列表
     */
    List<Announcement> getAnnouncementsByType(String announcementType);

    /**
     * 获取最新公告列表
     * 
     * @param limit 限制数量
     * @return 最新公告列表
     */
    List<Announcement> getLatestAnnouncements(int limit);

    /**
     * 按发布人获取公告列表
     * 
     * @param publisherId 发布人ID
     * @return 指定发布人的公告列表
     */
    List<Announcement> getAnnouncementsByPublisherId(Long publisherId);

    /**
     * 发布公告
     * 
     * @param id 公告ID
     * @return 操作是否成功
     */
    boolean publishAnnouncement(Long id);

    /**
     * 取消发布公告
     * 
     * @param id 公告ID
     * @return 操作是否成功
     */
    boolean unpublishAnnouncement(Long id);

    /**
     * 增加公告浏览次数
     * 
     * @param id 公告ID
     * @return 操作是否成功
     */
    boolean increaseViewCount(Long id);

    /**
     * 搜索公告
     * 
     * @param keyword 关键词
     * @return 匹配的公告列表
     */
    List<Announcement> searchAnnouncements(String keyword);

    /**
     * 获取公告详情（含发布人信息）
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    Announcement getAnnouncementWithPublisher(Long id);

    /**
     * 批量删除公告
     * 
     * @param ids 公告ID列表
     * @return 操作是否成功
     */
    boolean batchDeleteAnnouncements(List<Long> ids);

    /**
     * 批量发布公告
     * 
     * @param ids 公告ID列表
     * @return 操作是否成功
     */
    boolean batchPublishAnnouncements(List<Long> ids);

    /**
     * 批量取消发布公告
     * 
     * @param ids 公告ID列表
     * @return 操作是否成功
     */
    boolean batchUnpublishAnnouncements(List<Long> ids);
}