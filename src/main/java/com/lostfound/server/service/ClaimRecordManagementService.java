package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.ClaimRecord;

import java.util.List;

/**
 * 认领管理服务接口
 * 定义认领管理的通用方法，避免在多个服务中重复实现
 */
public interface ClaimRecordManagementService {
    
    /**
     * 获取认领记录分页数据
     */
    Page<ClaimRecord> getClaimRecordPage(Integer pageNum, Integer pageSize, String status);
    
    /**
     * 根据ID获取认领记录
     */
    ClaimRecord getClaimRecordById(Long id);
    
    /**
     * 根据物品ID获取认领记录
     */
    List<ClaimRecord> getClaimRecordsByItemId(Long itemId);
    
    /**
     * 根据用户ID获取认领记录
     */
    List<ClaimRecord> getClaimRecordsByUserId(Long userId);
    
    /**
     * 根据物品创建者ID获取认领记录
     */
    List<ClaimRecord> getClaimRecordsByCreatorId(Long creatorId);
    
    /**
     * 根据状态获取认领记录
     */
    List<ClaimRecord> getClaimRecordsByStatus(String status);
    
    /**
     * 保存认领记录
     */
    boolean saveClaimRecord(ClaimRecord claimRecord);
    
    /**
     * 更新认领记录
     */
    boolean updateClaimRecord(ClaimRecord claimRecord);
    
    /**
     * 根据ID删除认领记录
     */
    boolean deleteClaimRecordById(Long id);
    
    /**
     * 处理认领申请（批准/拒绝）
     */
    boolean processClaim(Long id, String status, String adminRemark);
    
    /**
     * 获取所有认领记录数量
     */
    long countClaimRecords();
    
    /**
     * 根据状态获取认领记录数量
     */
    long countClaimRecordsByStatus(String status);
}