package com.lostfound.server.controller;

import com.lostfound.server.util.Result;
import com.lostfound.server.dto.*;
import com.lostfound.server.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计数据控制器
 * 
 * 提供各类统计数据的API接口
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Tag(name = "数据统计管理")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取系统统计数据
     */
    @Operation(summary = "获取系统统计数据", description = "获取系统整体统计数据，包括用户总数、失物总数、认领记录总数等")
    @GetMapping("/system")
    public Result<SystemStatisticsDTO> getSystemStatistics() {
        SystemStatisticsDTO statistics = statisticsService.getSystemStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 获取失物类型分布统计
     */
    @Operation(summary = "获取失物类型分布统计", description = "获取各类型失物的数量分布和占比")
    @GetMapping("/item-types")
    public Result<List<ItemTypeDistributionDTO>> getItemTypeDistribution() {
        List<ItemTypeDistributionDTO> distribution = statisticsService.getItemTypeDistribution();
        return Result.success(distribution);
    }
    
    /**
     * 获取高发丢失地点统计
     */
    @Operation(summary = "获取高发丢失地点统计", description = "获取失物高发地点的统计数据")
    @GetMapping("/hot-locations")
    public Result<List<HotLocationDTO>> getHotLocations(
            @Parameter(description = "返回记录数限制", example = "10") 
            @RequestParam(defaultValue = "10") Integer limit) {
        List<HotLocationDTO> hotLocations = statisticsService.getHotLocations(limit);
        return Result.success(hotLocations);
    }
    
    /**
     * 获取失物状态统计
     */
    @Operation(summary = "获取失物状态统计", description = "获取各状态失物的数量分布和占比")
    @GetMapping("/item-status")
    public Result<List<ItemStatusStatisticsDTO>> getItemStatusStatistics() {
        List<ItemStatusStatisticsDTO> statusStatistics = statisticsService.getItemStatusStatistics();
        return Result.success(statusStatistics);
    }
    
    /**
     * 获取月度失物统计
     */
    @Operation(summary = "获取月度失物统计", description = "获取最近几个月的失物统计数据")
    @GetMapping("/monthly-items")
    public Result<List<MonthlyItemStatisticsDTO>> getMonthlyItemStatistics(
            @Parameter(description = "获取最近几个月的数据", example = "12") 
            @RequestParam(defaultValue = "12") Integer months) {
        List<MonthlyItemStatisticsDTO> monthlyStatistics = statisticsService.getMonthlyItemStatistics(months);
        return Result.success(monthlyStatistics);
    }
    
    /**
     * 获取指定年份的月度失物统计
     */
    @Operation(summary = "获取指定年份的月度失物统计", description = "获取指定年份的月度失物统计数据")
    @GetMapping("/yearly-items/{year}")
    public Result<List<MonthlyItemStatisticsDTO>> getYearlyItemStatistics(
            @Parameter(description = "年份", example = "2023", required = true) 
            @PathVariable Integer year) {
        List<MonthlyItemStatisticsDTO> yearlyStatistics = statisticsService.getYearlyItemStatistics(year);
        return Result.success(yearlyStatistics);
    }
}