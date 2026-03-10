package com.lostfound.server.dto;

import lombok.Data;

/**
 * 系统统计数据DTO
 * 
 * 用于返回系统整体统计数据
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class SystemStatisticsDTO {
    /**
     * 用户总数
     */
    private Long totalUsers;
    
    /**
     * 失物总数
     */
    private Long totalLostItems;
    
    /**
     * 认领记录总数
     */
    private Long totalClaimRecords;
    
    /**
     * 已归还失物数量
     */
    private Long returnedItems;
    
    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;
    
    /**
     * 今日新增失物数
     */
    private Long todayNewLostItems;
    
    /**
     * 今日新增认领数
     */
    private Long todayNewClaimRecords;
    
    /**
     * 待处理认领数
     */
    private Long pendingClaimRecords;
    
    /**
     * 本月新增用户数
     */
    private Long monthNewUsers;
    
    /**
     * 本月新增失物数
     */
    private Long monthNewLostItems;
    
    /**
     * 本月新增认领数
     */
    private Long monthNewClaimRecords;
    
    /**
     * 本月归还失物数
     */
    private Long monthReturnedItems;
}