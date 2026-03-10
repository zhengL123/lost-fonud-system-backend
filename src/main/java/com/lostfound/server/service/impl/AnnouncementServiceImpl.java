package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.Announcement;
import com.lostfound.server.mapper.AnnouncementMapper;
import com.lostfound.server.service.AnnouncementService;
import com.lostfound.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统公告Service实现类
 * 
 * 实现系统公告的业务逻辑操作
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private UserService userService;

    @Override
    public Page<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize, String status, String announcementType, String title) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        if (StringUtils.hasText(announcementType)) {
            queryWrapper.eq("announcement_type", announcementType);
        }
        
        if (StringUtils.hasText(title)) {
            queryWrapper.like("title", title);
        }
        
        queryWrapper.orderByDesc("publish_time");
        
        Page<Announcement> resultPage = this.page(page, queryWrapper);
        
        // 为每个公告设置发布人信息
        List<Announcement> records = resultPage.getRecords();
        for (Announcement announcement : records) {
            // 始终设置发布人为"管理员"
            announcement.setPublisherName("管理员");
        }
        
        return resultPage;
    }

    @Override
    public List<Announcement> getPublishedAnnouncements() {
        return baseMapper.selectPublishedAnnouncements();
    }

    @Override
    public List<Announcement> getAnnouncementsByType(String announcementType) {
        return baseMapper.selectAnnouncementsByType(announcementType);
    }

    @Override
    public List<Announcement> getLatestAnnouncements(int limit) {
        return baseMapper.selectLatestAnnouncements(limit);
    }

    @Override
    public List<Announcement> getAnnouncementsByPublisherId(Long publisherId) {
        return baseMapper.selectAnnouncementsByPublisherId(publisherId);
    }

    @Override
    @Transactional
    public boolean publishAnnouncement(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            return false;
        }
        
        announcement.setStatus("PUBLISHED");
        if (announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        
        return this.updateById(announcement);
    }

    @Override
    @Transactional
    public boolean unpublishAnnouncement(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            return false;
        }
        
        announcement.setStatus("DRAFT");
        return this.updateById(announcement);
    }

    @Override
    public boolean increaseViewCount(Long id) {
        return baseMapper.increaseViewCount(id) > 0;
    }

    @Override
    public List<Announcement> searchAnnouncements(String keyword) {
        return baseMapper.searchAnnouncements(keyword);
    }

    @Override
    public Announcement getAnnouncementWithPublisher(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement != null) {
            // 始终设置发布人为"管理员"
            announcement.setPublisherName("管理员");
        }
        return announcement;
    }

    @Override
    @Transactional
    public boolean batchDeleteAnnouncements(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    @Transactional
    public boolean batchPublishAnnouncements(List<Long> ids) {
        List<Announcement> announcements = this.listByIds(ids);
        LocalDateTime now = LocalDateTime.now();
        
        for (Announcement announcement : announcements) {
            announcement.setStatus("PUBLISHED");
            if (announcement.getPublishTime() == null) {
                announcement.setPublishTime(now);
            }
        }
        
        return this.updateBatchById(announcements);
    }

    @Override
    @Transactional
    public boolean batchUnpublishAnnouncements(List<Long> ids) {
        List<Announcement> announcements = this.listByIds(ids);
        
        for (Announcement announcement : announcements) {
            announcement.setStatus("DRAFT");
        }
        
        return this.updateBatchById(announcements);
    }
}