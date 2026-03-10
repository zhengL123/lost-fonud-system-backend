package com.lostfound.server.dto;

import lombok.Data;

/**
 * 失物类型分布统计数据DTO
 * 
 * 用于返回各类型失物的数量分布
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class ItemTypeDistributionDTO {
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 该类型失物数量
     */
    private Long count;
    
    /**
     * 占总数的百分比
     */
    private Double percentage;
}