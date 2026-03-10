package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.ClaimRecord;
import com.lostfound.server.service.ClaimRecordService;
import com.lostfound.server.util.ClaimRecordServiceHelper;
import com.lostfound.server.util.Result;
import com.lostfound.server.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 认领记录控制器
 */
@RestController
@RequestMapping("/api/claim-records")
@RequiredArgsConstructor
public class ClaimRecordController {

    private final ClaimRecordService claimRecordService;
    private final ClaimRecordServiceHelper claimRecordServiceHelper;

    /**
     * 创建认领申请
     */
    @PostMapping
    public Result<String> createClaimRecord(@RequestBody ClaimRecord claimRecord) {
        // 验证认领记录信息
        Result<String> validationResult = claimRecordServiceHelper.validateClaimRecord(claimRecord);
        if (validationResult.getCode() != 200) {
            return Result.error(validationResult.getMessage());
        }
        
        claimRecord.setStatus("PENDING");
        boolean saved = claimRecordService.save(claimRecord);
        return saved ? 
            Result.success("认领申请提交成功") : 
            Result.error("提交失败");
    }

    /**
     * 分页查询认领记录
     */
    @GetMapping("/page")
    public Result<PageResult<ClaimRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String claimUserName) {

        // 验证分页参数
        Result<String> validationResult = claimRecordServiceHelper.validatePageParams(pageNum, pageSize);
        if (validationResult.getCode() != 200) {
            return Result.error(validationResult.getMessage());
        }

        Page<ClaimRecord> result;

        if (status != null) {
            // 使用带关联数据的方法获取记录
            List<ClaimRecord> records = claimRecordService.getRecordsByStatusWithDetails(status);
            
            // 根据物品名称和认领人名称过滤
            if (itemName != null && !itemName.trim().isEmpty()) {
                records = records.stream()
                    .filter(r -> r.getItemName() != null && r.getItemName().contains(itemName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (claimUserName != null && !claimUserName.trim().isEmpty()) {
                records = records.stream()
                    .filter(r -> r.getClaimUserName() != null && r.getClaimUserName().contains(claimUserName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            result = new Page<>(pageNum, pageSize);
            result.setRecords(records);
            result.setTotal(records.size());
        } else {
            // 使用自定义查询方法获取关联数据
            List<ClaimRecord> allRecords = claimRecordService.getAllRecordsWithDetails();
            
            // 根据物品名称和认领人名称过滤
            if (itemName != null && !itemName.trim().isEmpty()) {
                allRecords = allRecords.stream()
                    .filter(r -> r.getItemName() != null && r.getItemName().contains(itemName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            if (claimUserName != null && !claimUserName.trim().isEmpty()) {
                allRecords = allRecords.stream()
                    .filter(r -> r.getClaimUserName() != null && r.getClaimUserName().contains(claimUserName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            result = new Page<>(pageNum, pageSize);
            
            // 手动分页
            int total = allRecords.size();
            int start = (pageNum - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            if (start < total) {
                result.setRecords(allRecords.subList(start, end));
            } else {
                result.setRecords(new ArrayList<>());
            }
            result.setTotal(total);
        }

        // 转换为自定义分页响应格式
        PageResult<ClaimRecord> pageResult = PageResult.of(result);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询认领记录详情
     */
    @GetMapping("/{id}")
    public Result<ClaimRecord> getById(@PathVariable Long id) {
        ClaimRecord record = claimRecordService.getRecordByIdWithDetails(id);
        return record != null ? 
            Result.success(record) : 
            Result.error("认领记录不存在");
    }

    /**
     * 更新认领记录
     */
    @PutMapping("/{id}")
    public Result<String> updateById(@PathVariable Long id, @RequestBody ClaimRecord claimRecord) {
        // 验证认领记录信息
        Result<String> validationResult = claimRecordServiceHelper.validateClaimRecord(claimRecord);
        if (validationResult.getCode() != 200) {
            return Result.error(validationResult.getMessage());
        }
        
        claimRecord.setId(id);
        boolean updated = claimRecordService.updateById(claimRecord);
        return updated ? 
            Result.success("更新成功") : 
            Result.error("更新失败");
    }

    /**
     * 删除认领记录
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        boolean removed = claimRecordService.removeById(id);
        return removed ? 
            Result.success("删除成功") : 
            Result.error("删除失败");
    }

    /**
     * 根据物品ID查询认领记录
     */
    @GetMapping("/item/{itemId}")
    public Result<List<ClaimRecord>> getByItemId(@PathVariable Long itemId) {
        List<ClaimRecord> records = claimRecordService.getRecordsByItemId(itemId);
        return Result.success(records);
    }

    /**
     * 根据用户ID查询认领记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<ClaimRecord>> getByUserId(@PathVariable Long userId) {
        List<ClaimRecord> records = claimRecordService.getRecordsByUserId(userId);
        return Result.success(records);
    }
    
    /**
     * 根据物品创建者ID查询认领记录
     */
    @GetMapping("/creator/{creatorId}")
    public Result<List<ClaimRecord>> getByCreatorId(@PathVariable Long creatorId) {
        List<ClaimRecord> records = claimRecordService.getRecordsByCreatorId(creatorId);
        return Result.success(records);
    }

    /**
     * 根据状态查询认领记录
     */
    @GetMapping("/status/{status}")
    public Result<List<ClaimRecord>> getByStatus(@PathVariable String status) {
        List<ClaimRecord> records = claimRecordService.getRecordsByStatus(status);
        return Result.success(records);
    }

    /**
     * 处理认领申请（管理员操作）
     */
    @PutMapping("/{id}/process")
    public Result<String> processClaim(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String adminRemark) {

        // 验证处理参数
        Result<String> validationResult = claimRecordServiceHelper.validateProcessClaim(status);
        if (validationResult.getCode() != 200) {
            return Result.error(validationResult.getMessage());
        }
        
        boolean processed = claimRecordService.processClaim(id, status, adminRemark);
        return processed ? 
            Result.success("处理成功") : 
            Result.error("处理失败");
    }

    /**
     * 获取待处理的认领申请
     */
    @GetMapping("/pending")
    public Result<List<ClaimRecord>> getPendingRecords() {
        List<ClaimRecord> records = claimRecordService.getRecordsByStatus("PENDING");
        return Result.success(records);
    }
}