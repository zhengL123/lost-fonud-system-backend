package com.lostfound.server.service.impl;

import com.lostfound.server.dto.*;
import com.lostfound.server.mapper.StatisticsMapper;
import com.lostfound.server.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 统计数据服务实现类
 * 
 * 实现各类统计数据的业务逻辑处理
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private StatisticsMapper statisticsMapper;
    
    @Override
    public SystemStatisticsDTO getSystemStatistics() {
        return statisticsMapper.getSystemStatistics();
    }
    
    @Override
    public List<ItemTypeDistributionDTO> getItemTypeDistribution() {
        List<ItemTypeDistributionDTO> distribution = statisticsMapper.getItemTypeDistribution();
        
        // 计算百分比
        if (distribution != null && !distribution.isEmpty()) {
            long total = distribution.stream().mapToLong(ItemTypeDistributionDTO::getCount).sum();
            
            for (ItemTypeDistributionDTO item : distribution) {
                double percentage = (double) item.getCount() / total * 100;
                item.setPercentage(Math.round(percentage * 100.0) / 100.0); // 保留两位小数
            }
        }
        
        return distribution;
    }
    
    @Override
    public List<HotLocationDTO> getHotLocations(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回前10个
        }
        
        List<HotLocationDTO> hotLocations = statisticsMapper.getHotLocations(limit);
        
        // 计算百分比
        if (hotLocations != null && !hotLocations.isEmpty()) {
            long total = hotLocations.stream().mapToLong(HotLocationDTO::getCount).sum();
            
            for (HotLocationDTO location : hotLocations) {
                double percentage = (double) location.getCount() / total * 100;
                location.setPercentage(Math.round(percentage * 100.0) / 100.0); // 保留两位小数
            }
        }
        
        return hotLocations;
    }
    
    @Override
    public List<ItemStatusStatisticsDTO> getItemStatusStatistics() {
        List<ItemStatusStatisticsDTO> statusStatistics = statisticsMapper.getItemStatusStatistics();
        
        // 设置状态名称
        if (statusStatistics != null) {
            for (ItemStatusStatisticsDTO status : statusStatistics) {
                switch (status.getStatus()) {
                    case 0:
                        status.setStatusName("未领取");
                        break;
                    case 1:
                        status.setStatusName("已认领");
                        break;
                    case 2:
                        status.setStatusName("已归还");
                        break;
                    default:
                        status.setStatusName("未知状态");
                        break;
                }
            }
            
            // 计算百分比
            if (!statusStatistics.isEmpty()) {
                long total = statusStatistics.stream().mapToLong(ItemStatusStatisticsDTO::getCount).sum();
                
                for (ItemStatusStatisticsDTO status : statusStatistics) {
                    double percentage = (double) status.getCount() / total * 100;
                    status.setPercentage(Math.round(percentage * 100.0) / 100.0); // 保留两位小数
                }
            }
        }
        
        return statusStatistics;
    }
    
    @Override
    public List<MonthlyItemStatisticsDTO> getMonthlyItemStatistics(Integer months) {
        if (months == null || months <= 0) {
            months = 12; // 默认返回最近12个月
        }
        
        return statisticsMapper.getMonthlyItemStatistics(months);
    }
    
    @Override
    public List<MonthlyItemStatisticsDTO> getYearlyItemStatistics(Integer year) {
        if (year == null) {
            year = java.time.Year.now().getValue(); // 默认当前年份
        }
        
        return statisticsMapper.getYearlyItemStatistics(year);
    }
}