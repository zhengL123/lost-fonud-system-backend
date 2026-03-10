package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统公告Mapper接口
 * 
 * 提供系统公告的数据库访问操作
 *
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 查询已发布的公告列表
     * 
     * @return 公告列表
     */
    @Select("SELECT * FROM announcements WHERE status = 'PUBLISHED' ORDER BY publish_time DESC")
    List<Announcement> selectPublishedAnnouncements();

    /**
     * 按类型查询公告列表
     * 
     * @param announcementType 公告类型
     * @return 指定类型的公告列表
     */
    @Select("SELECT * FROM announcements WHERE announcement_type = #{announcementType} AND status = 'PUBLISHED' ORDER BY publish_time DESC")
    List<Announcement> selectAnnouncementsByType(@Param("announcementType") String announcementType);

    /**
     * 增加公告浏览次数
     * 
     * @param id 公告ID
     * @return 影响的行数
     */
    @Update("UPDATE announcements SET view_count = IFNULL(view_count, 0) + 1 WHERE id = #{id}")
    int increaseViewCount(@Param("id") Long id);

    /**
     * 查询最新公告列表
     * 
     * @param limit 限制数量
     * @return 最新公告列表
     */
    @Select("SELECT * FROM announcements WHERE status = 'PUBLISHED' ORDER BY publish_time DESC LIMIT #{limit}")
    List<Announcement> selectLatestAnnouncements(@Param("limit") int limit);

    /**
     * 按发布人查询公告列表
     * 
     * @param publisherId 发布人ID
     * @return 指定发布人的公告列表
     */
    @Select("SELECT * FROM announcements WHERE publisher_id = #{publisherId} ORDER BY created_time DESC")
    List<Announcement> selectAnnouncementsByPublisherId(@Param("publisherId") Long publisherId);

    /**
     * 模糊搜索公告
     * 
     * @param keyword 关键词
     * @return 匹配的公告列表
     */
    @Select("SELECT * FROM announcements WHERE status = 'PUBLISHED' AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) ORDER BY publish_time DESC")
    List<Announcement> searchAnnouncements(@Param("keyword") String keyword);
}