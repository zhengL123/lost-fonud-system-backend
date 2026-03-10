package com.lostfound.server.dto;

import lombok.Data;

/**
 * 高发丢失地点统计数据DTO
 * 
 * 用于返回失物高发地点的统计数据
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class HotLocationDTO {
    /**
     * 地点名称
     */
    private String location;
    
    /**
     * 该地点失物数量
     */
    private Long count;
    
    /**
     * 占总数的百分比
     */
    private Double percentage;
}