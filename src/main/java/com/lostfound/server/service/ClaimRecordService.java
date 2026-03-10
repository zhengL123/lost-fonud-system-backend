package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.ClaimRecord;
import java.util.List;

/**
 * 认领记录服务接口
 */
public interface ClaimRecordService extends IService<ClaimRecord> {

    /**
     * 根据物品ID查询认领记录
     * @param itemId 物品ID
     * @return 认领记录列表
     */
    List<ClaimRecord> getRecordsByItemId(Long itemId);

    /**
     * 根据用户ID查询认领记录
     * @param claimUserId 认领用户ID
     * @return 认领记录列表
     */
    List<ClaimRecord> getRecordsByUserId(Long claimUserId);
    
    /**
     * 根据物品创建者ID查询认领记录
     * @param creatorId 物品创建者ID
     * @return 认领记录列表
     */
    List<ClaimRecord> getRecordsByCreatorId(Long creatorId);

    /**
     * 根据状态查询认领记录
     * @param status 状态
     * @return 认领记录列表
     */
    List<ClaimRecord> getRecordsByStatus(String status);
    
    /**
     * 根据状态查询认领记录（包含关联数据）
     * @param status 状态
     * @return 认领记录列表
     */
    List<ClaimRecord> getRecordsByStatusWithDetails(String status);
    
    /**
     * 获取所有认领记录（包含关联数据）
     * @return 认领记录列表
     */
    List<ClaimRecord> getAllRecordsWithDetails();

    /**
     * 处理认领申请
     * @param recordId 记录ID
     * @param status 处理状态
     * @param adminRemark 管理员备注
     * @return 是否成功
     */
    boolean processClaim(Long recordId, String status, String adminRemark);
    
    /**
     * 根据ID获取认领记录（包含关联数据）
     * @param id 记录ID
     * @return 认领记录
     */
    ClaimRecord getRecordByIdWithDetails(Long id);
}