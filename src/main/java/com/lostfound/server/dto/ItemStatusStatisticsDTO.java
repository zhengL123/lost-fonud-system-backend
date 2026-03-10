package com.lostfound.server.dto;

import lombok.Data;

/**
 * 失物状态统计数据DTO
 * 
 * 用于返回各状态失物的数量分布
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class ItemStatusStatisticsDTO {
    /**
     * 状态值
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 该状态失物数量
     */
    private Long count;
    
    /**
     * 占总数的百分比
     */
    private Double percentage;
}