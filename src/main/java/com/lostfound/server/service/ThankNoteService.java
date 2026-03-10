package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.ThankNote;
import java.util.List;

/**
 * 感谢信服务接口
 */
public interface ThankNoteService extends IService<ThankNote> {

    /**
     * 分页查询感谢信
     * @param page 分页参数
     * @param rating 评分（可选）
     * @return 分页结果
     */
    Page<ThankNote> getThankNotePage(Page<ThankNote> page, Integer rating);

    /**
     * 根据认领记录ID查询感谢信
     * @param recordId 认领记录ID
     * @return 感谢信列表
     */
    List<ThankNote> getThankNotesByRecordId(Long recordId);

    /**
     * 根据评分查询感谢信
     * @param rating 评分
     * @return 感谢信列表
     */
    List<ThankNote> getThankNotesByRating(Integer rating);

    /**
     * 获取平均评分
     * @return 平均评分
     */
    Double getAverageRating();

    /**
     * 根据物品ID获取感谢信
     * @param itemId 物品ID
     * @return 感谢信列表
     */
    List<ThankNote> getThankNotesByItemId(Long itemId);

    /**
     * 根据用户ID获取感谢信
     * @param userId 用户ID
     * @return 感谢信列表
     */
    List<ThankNote> getThankNotesByUserId(Long userId);

    /**
     * 获取最新的感谢信
     * @param limit 限制数量
     * @return 感谢信列表
     */
    List<ThankNote> getLatestNotes(Integer limit);
}