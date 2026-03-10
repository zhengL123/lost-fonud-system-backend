package com.lostfound.server.util;

import com.lostfound.server.entity.ClaimRecord;
import com.lostfound.server.service.ClaimRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 认领记录服务工具类
 */
@Component
public class ClaimRecordServiceHelper {
    
    @Autowired
    private ClaimRecordService claimRecordService;
    
    /**
     * 验证认领记录信息
     */
    public Result<String> validateClaimRecord(ClaimRecord claimRecord) {
        if (claimRecord == null) {
            return Result.error("认领记录信息不能为空");
        }
        
        if (claimRecord.getItemId() == null) {
            return Result.error("物品ID不能为空");
        }
        
        if (claimRecord.getClaimUserId() == null) {
            return Result.error("认领用户ID不能为空");
        }
        
        if (!StringUtils.hasText(claimRecord.getClaimReason())) {
            return Result.error("认领原因不能为空");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 验证处理认领申请的参数
     */
    public Result<String> validateProcessClaim(String status) {
        if (!StringUtils.hasText(status)) {
            return Result.error("状态不能为空");
        }
        
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status) && !"COMPLETED".equals(status)) {
            return Result.error("状态只能是APPROVED、REJECTED或COMPLETED");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 创建成功响应
     */
    public ResponseEntity<String> createSuccessResponse(String message) {
        return ResponseEntity.ok(message);
    }
    
    /**
     * 创建失败响应
     */
    public ResponseEntity<String> createFailureResponse(String message) {
        return ResponseEntity.badRequest().body(message);
    }
    
    /**
     * 创建成功响应（带数据）
     */
    public <T> ResponseEntity<T> createSuccessResponse(T data) {
        return ResponseEntity.ok(data);
    }
    
    /**
     * 创建404响应
     */
    public <T> ResponseEntity<T> createNotFoundResponse() {
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 处理分页查询
     */
    public Result<String> validatePageParams(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            return Result.error("页码必须大于0");
        }
        
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            return Result.error("每页数量必须在1-100之间");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 检查字符串是否为空
     */
    public boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串是否不为空
     */
    public boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }
}