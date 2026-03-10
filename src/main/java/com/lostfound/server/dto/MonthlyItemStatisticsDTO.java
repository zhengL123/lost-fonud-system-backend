package com.lostfound.server.dto;

import lombok.Data;

/**
 * 月度失物统计数据DTO
 * 
 * 用于返回月度失物统计信息
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class MonthlyItemStatisticsDTO {
    /**
     * 年份
     */
    private Integer year;
    
    /**
     * 月份
     */
    private Integer month;
    
    /**
     * 该月失物数量
     */
    private Long count;
    
    /**
     * 该月认领数量
     */
    private Long claimCount;
    
    /**
     * 该月归还数量
     */
    private Long returnCount;
}