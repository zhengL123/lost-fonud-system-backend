package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.ThankNote;
import com.lostfound.server.mapper.ThankNoteMapper;
import com.lostfound.server.service.ThankNoteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThankNoteServiceImpl extends ServiceImpl<ThankNoteMapper, ThankNote> implements ThankNoteService {

    @Override
    public Page<ThankNote> getThankNotePage(Page<ThankNote> page, Integer rating) {
        LambdaQueryWrapper<ThankNote> queryWrapper = new LambdaQueryWrapper<>();
        if (rating != null) {
            queryWrapper.eq(ThankNote::getRating, rating);
        }
        queryWrapper.orderByDesc(ThankNote::getCreatedTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<ThankNote> getThankNotesByRecordId(Long recordId) {
        return baseMapper.selectByRecordId(recordId);
    }

    /**
     * 获取高评分感谢信（4分以上）
     */
    @Override
    public List<ThankNote> getThankNotesByRating(Integer rating) {
        if (rating >= 4) {
            return baseMapper.selectHighRatingNotes();
        } else {
            return baseMapper.selectByRating(rating);
        }
    }

    /**
     * 根据物品ID获取感谢信
     */
    @Override
    public List<ThankNote> getThankNotesByItemId(Long itemId) {
        return baseMapper.selectByItemId(itemId);
    }

    /**
     * 根据用户ID获取感谢信
     */
    @Override
    public List<ThankNote> getThankNotesByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public Double getAverageRating() {
        return baseMapper.selectAverageRating();
    }

    @Override
    public List<ThankNote> getLatestNotes(Integer limit) {
        return baseMapper.selectLatestNotes(limit);
    }
}