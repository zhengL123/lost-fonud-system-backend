package com.lostfound.server.controller;

import com.lostfound.server.entity.LostItem;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.util.RedisService;
import com.lostfound.server.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis使用示例控制器
 * 
 * 展示如何在控制器中使用Redis缓存和RedisService工具类
 * 
 * @author fzl
 */
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
@Tag(name = "Redis示例", description = "Redis使用示例接口")
public class RedisExampleController {

    private final LostItemService lostItemService;
    private final RedisService redisService;

    /**
     * 获取热门失物（使用Redis缓存）
     * 
     * 从Redis缓存中获取热门失物列表，如果缓存不存在则从数据库查询并缓存
     * 缓存有效期为30分钟
     * 
     * @return 热门失物列表
     */
    @GetMapping("/hot-items")
    @Operation(summary = "获取热门失物", description = "获取热门失物列表，使用Redis缓存")
    public Result<List<LostItem>> getHotItems() {
        // 尝试从Redis获取缓存
        String cacheKey = "hot:lost_items";
        List<LostItem> hotItems = (List<LostItem>) redisService.get(cacheKey);
        
        if (hotItems == null) {
            // 缓存不存在，从数据库查询
            // 这里假设有一个获取热门失物的方法，实际应用中可以根据浏览量、点赞数等排序
            hotItems = lostItemService.list();
            
            // 将结果存入Redis，设置30分钟过期
            redisService.set(cacheKey, hotItems, 30, TimeUnit.MINUTES);
        }
        
        return Result.success(hotItems);
    }

    /**
     * 记录用户浏览历史（使用Redis列表）
     * 
     * 将用户浏览的失物ID记录到Redis列表中，最多保存最近20条记录
     * 
     * @param userId 用户ID
     * @param itemId 失物ID
     * @return 操作结果
     */
    @PostMapping("/view-history")
    @Operation(summary = "记录浏览历史", description = "记录用户浏览失物的历史，使用Redis列表存储")
    public Result<String> recordViewHistory(@RequestParam Long userId, @RequestParam Long itemId) {
        // 构建浏览历史键名
        String historyKey = "user:view_history:" + userId;
        
        // 将失物ID添加到列表头部
        redisService.lSet(historyKey, itemId);
        
        // 只保留最近20条记录
        redisService.lGet(historyKey, 0, 19);
        
        // 设置过期时间为7天
        redisService.expire(historyKey, 7, TimeUnit.DAYS);
        
        return Result.success("浏览历史记录成功");
    }

    /**
     * 获取用户浏览历史（使用Redis列表）
     * 
     * 获取用户最近浏览的失物ID列表
     * 
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    @GetMapping("/view-history/{userId}")
    @Operation(summary = "获取浏览历史", description = "获取用户最近浏览的失物历史，从Redis列表中获取")
    public Result<List<Object>> getViewHistory(@PathVariable Long userId) {
        String historyKey = "user:view_history:" + userId;
        
        // 获取最近20条浏览记录
        List<Object> history = redisService.lGet(historyKey, 0, 19);
        
        return Result.success(history);
    }

    /**
     * 统计失物浏览次数（使用Redis计数器）
     * 
     * 使用Redis计数器统计每个失物的浏览次数
     * 
     * @param itemId 失物ID
     * @return 浏览次数
     */
    @GetMapping("/view-count/{itemId}")
    @Operation(summary = "获取浏览次数", description = "获取失物的浏览次数，使用Redis计数器统计")
    public Result<Long> getViewCount(@PathVariable Long itemId) {
        String countKey = "item:view_count:" + itemId;
        
        // 增加浏览次数
        Long count = redisService.increment(countKey, 1);
        
        // 设置过期时间为30天
        redisService.expire(countKey, 30, TimeUnit.DAYS);
        
        return Result.success(count);
    }

    /**
     * 用户点赞失物（使用Redis集合）
     * 
     * 使用Redis集合记录用户点赞的失物，防止重复点赞
     * 
     * @param userId 用户ID
     * @param itemId 失物ID
     * @return 操作结果
     */
    @PostMapping("/like")
    @Operation(summary = "点赞失物", description = "用户点赞失物，使用Redis集合防止重复点赞")
    public Result<String> likeItem(@RequestParam Long userId, @RequestParam Long itemId) {
        String likeKey = "item:likes:" + itemId;
        
        // 检查用户是否已点赞
        boolean hasLiked = redisService.sHasKey(likeKey, userId.toString());
        
        if (hasLiked) {
            return Result.error("您已经点赞过了");
        }
        
        // 添加点赞记录
        redisService.sSet(likeKey, userId.toString());
        
        // 设置过期时间为30天
        redisService.expire(likeKey, 30, TimeUnit.DAYS);
        
        return Result.success("点赞成功");
    }

    /**
     * 获取失物点赞数（使用Redis集合）
     * 
     * 获取失物的点赞总数
     * 
     * @param itemId 失物ID
     * @return 点赞数
     */
    @GetMapping("/like-count/{itemId}")
    @Operation(summary = "获取点赞数", description = "获取失物的点赞总数，从Redis集合中计算")
    public Result<Long> getLikeCount(@PathVariable Long itemId) {
        String likeKey = "item:likes:" + itemId;
        
        // 获取点赞数
        Long count = redisService.sGetSetSize(likeKey);
        
        return Result.success(count);
    }

    /**
     * 清除指定缓存
     * 
     * 清除指定的Redis缓存，用于测试和管理
     * 
     * @param key 缓存键
     * @return 操作结果
     */
    @DeleteMapping("/cache/{key}")
    @Operation(summary = "清除缓存", description = "清除指定的Redis缓存")
    public Result<String> clearCache(@PathVariable String key) {
        boolean deleted = redisService.delete(key);
        
        if (deleted) {
            return Result.success("缓存清除成功");
        } else {
            return Result.error("缓存不存在或清除失败");
        }
    }
}