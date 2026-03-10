package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告实体类
 * 
 * 对应数据库表 announcements，存储系统公告信息
 * 包含公告标题、内容、发布状态、发布时间等字段
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
@TableName("announcements")
public class Announcement {
    /**
     * 公告ID
     * 主键，自增长
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     * 公告的标题，简明扼要地概括公告内容
     */
    @TableField("title")
    private String title;

    /**
     * 公告内容
     * 公告的详细内容，支持HTML格式
     */
    @TableField("content")
    private String content;

    /**
     * 公告类型
     * 可选值：
     * - SYSTEM: 系统公告
     * - MAINTENANCE: 维护通知
     * - ACTIVITY: 活动通知
     * - OTHER: 其他
     */
    @TableField("announcement_type")
    private String announcementType;

    /**
     * 发布状态
     * 可选值：
     * - DRAFT: 草稿
     * - PUBLISHED: 已发布
     * - EXPIRED: 已过期
     */
    @TableField("status")
    private String status = "DRAFT";

    /**
     * 发布时间
     * 公告发布的时间
     */
    @TableField("publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 发布人ID
     * 发布公告的管理员ID
     */
    @TableField("publisher_id")
    private Long publisherId;

    /**
     * 浏览次数
     * 公告被浏览的次数
     */
    @TableField("view_count")
    private Long viewCount = 0L;

    /**
     * 创建时间
     * 公告创建的时间
     * 插入数据时自动填充fill = FieldFill.INSERT
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     * 公告最后修改的时间
     * 插入和更新数据时自动填充fill = FieldFill.INSERT_UPDATE
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 关联对象（非数据库字段）

    /**
     * 发布人姓名
     * 关联查询用户表获取的管理员用户名
     * 不对应数据库字段，仅用于展示
     */
    @TableField(exist = false)
    private String publisherName;

    /**
     * 判断公告是否已发布
     * 
     * @return boolean true-已发布，false-未发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(this.status);
    }

    /**
     * 判断公告是否应该显示
     * 
     * @return boolean true-应该显示，false-不应该显示
     */
    public boolean shouldDisplay() {
        return isPublished();
    }
}