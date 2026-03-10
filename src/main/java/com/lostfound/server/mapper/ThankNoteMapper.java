package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.ThankNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 感谢信/评价表 Mapper 接口
 * 
 * 对应数据库表：thank_notes
 * 
 * 提供感谢信/评价相关的数据访问操作，包括：
 * 1. 基础CRUD操作（继承自BaseMapper）
 * 2. 按条件查询感谢信（认领记录ID、评分、用户ID、物品ID等）
 * 3. 查询高评分感谢信
 * 4. 统计平均评分和各评分数量
 * 5. 查询最新感谢信
 */
@Mapper
public interface ThankNoteMapper extends BaseMapper<ThankNote> {

    /**
     * 根据认领记录ID查询感谢信
     * @param recordId 认领记录ID
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "LEFT JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "WHERE tn.record_id = #{recordId} " +
            "ORDER BY tn.created_time DESC")
    List<ThankNote> selectByRecordId(@Param("recordId") Long recordId);

    /**
     * 根据评分查询感谢信
     * @param rating 评分
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "LEFT JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "WHERE tn.rating = #{rating} " +
            "ORDER BY tn.created_time DESC")
    List<ThankNote> selectByRating(@Param("rating") Integer rating);

    /**
     * 查询高评分感谢信（4分及以上）
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "LEFT JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "WHERE tn.rating >= 4 " +
            "ORDER BY tn.rating DESC, tn.created_time DESC")
    List<ThankNote> selectHighRatingNotes();

    /**
     * 统计平均评分
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM thank_notes WHERE rating IS NOT NULL")
    Double selectAverageRating();

    /**
     * 统计各评分数量
     * @return 评分统计列表
     */
    @Select("SELECT rating, COUNT(*) as count FROM thank_notes WHERE rating IS NOT NULL GROUP BY rating ORDER BY rating DESC")
    List<java.util.Map<String, Object>> countByRatingGroup();

    /**
     * 查询用户的所有感谢信
     * @param userId 用户ID（通过关联查询）
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "WHERE cr.claim_user_id = #{userId} " +
            "ORDER BY tn.created_time DESC")
    List<ThankNote> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据物品ID获取感谢信
     * @param itemId 物品ID
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "LEFT JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "WHERE cr.item_id = #{itemId} " +
            "ORDER BY tn.created_time DESC")
    List<ThankNote> selectByItemId(@Param("itemId") Long itemId);

    /**
     * 查询最新的感谢信
     * @param limit 限制数量
     * @return 感谢信列表
     */
    @Select("SELECT tn.*, li.item_name as itemName, u.username as claimUserName " +
            "FROM thank_notes tn " +
            "LEFT JOIN claim_records cr ON tn.record_id = cr.id " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users u ON cr.claim_user_id = u.id " +
            "ORDER BY tn.created_time DESC " +
            "LIMIT #{limit}")
    List<ThankNote> selectLatestNotes(@Param("limit") Integer limit);
}