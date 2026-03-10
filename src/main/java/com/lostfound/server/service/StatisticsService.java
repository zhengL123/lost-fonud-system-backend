package com.lostfound.server.service;

import com.lostfound.server.dto.*;

import java.util.List;

/**
 * 统计数据服务接口
 * 提供各类统计数据的业务逻辑处理
 */
public interface StatisticsService {
    
    /**
     * 获取系统统计数据
     * 
     * @return 系统统计数据
     */
    SystemStatisticsDTO getSystemStatistics();
    
    /**
     * 获取失物类型分布统计
     * 
     * @return 失物类型分布列表
     */
    List<ItemTypeDistributionDTO> getItemTypeDistribution();
    
    /**
     * 获取高发丢失地点统计
     * 
     * @param limit 返回记录数限制，默认10
     * @return 高发丢失地点列表
     */
    List<HotLocationDTO> getHotLocations(Integer limit);
    
    /**
     * 获取失物状态统计
     * 
     * @return 失物状态统计列表
     */
    List<ItemStatusStatisticsDTO> getItemStatusStatistics();
    
    /**
     * 获取月度失物统计
     * 
     * @param months 获取最近几个月的数据，默认12
     * @return 月度失物统计列表
     */
    List<MonthlyItemStatisticsDTO> getMonthlyItemStatistics(Integer months);
    
    /**
     * 获取指定年份的月度失物统计
     * 
     * @param year 年份
     * @return 月度失物统计列表
     */
    List<MonthlyItemStatisticsDTO> getYearlyItemStatistics(Integer year);
}