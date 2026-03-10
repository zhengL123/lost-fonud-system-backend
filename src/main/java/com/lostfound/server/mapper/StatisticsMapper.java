package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计数据Mapper接口
 * 
 * 提供各类统计数据的数据库查询操作
 */
@Mapper
public interface StatisticsMapper extends BaseMapper<Object> {
    
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
     * @param limit 返回记录数限制
     * @return 高发丢失地点列表
     */
    List<HotLocationDTO> getHotLocations(@Param("limit") Integer limit);
    
    /**
     * 获取失物状态统计
     * 
     * @return 失物状态统计列表
     */
    List<ItemStatusStatisticsDTO> getItemStatusStatistics();
    
    /**
     * 获取月度失物统计
     * 
     * @param months 获取最近几个月的数据
     * @return 月度失物统计列表
     */
    List<MonthlyItemStatisticsDTO> getMonthlyItemStatistics(@Param("months") Integer months);
    
    /**
     * 获取指定年份的月度失物统计
     * 
     * @param year 年份
     * @return 月度失物统计列表
     */
    List<MonthlyItemStatisticsDTO> getYearlyItemStatistics(@Param("year") Integer year);
}