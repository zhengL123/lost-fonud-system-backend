package com.lostfound.server.controller;

import com.lostfound.server.entity.LostItem;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.util.RedisService;
import com.lostfound.server.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 热门搜索控制器
 * 
 * 提供热门搜索关键词、搜索历史记录等功能
 * 
 * @author fzl
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "热门搜索", description = "热门搜索相关接口")
public class HotSearchController {

    private final LostItemService lostItemService;
    private final RedisService redisService;

    // 热门搜索关键词缓存键
    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";
    // 用户搜索历史键前缀
    private static final String USER_SEARCH_HISTORY_PREFIX = "user:search_history:";
    // 搜索关键词计数键前缀
    private static final String SEARCH_COUNT_PREFIX = "search:count:";

    /**
     * 记录用户搜索关键词
     * 
     * 记录用户搜索的关键词，并更新关键词搜索次数
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 操作结果
     */
    @PostMapping("/record")
    @Operation(summary = "记录搜索关键词", description = "记录用户搜索的关键词，并更新关键词搜索次数")
    public Result<String> recordSearchKeyword(@RequestParam Long userId, @RequestParam String keyword) {
        // 记录用户搜索历史
        String historyKey = USER_SEARCH_HISTORY_PREFIX + userId;
        redisService.lSet(historyKey, keyword);
        
        // 只保留最近20条搜索记录
        redisService.lGet(historyKey, 0, 19);
        
        // 设置过期时间为30天
        redisService.expire(historyKey, 30, TimeUnit.DAYS);
        
        // 更新关键词搜索次数
        String countKey = SEARCH_COUNT_PREFIX + keyword;
        redisService.increment(countKey, 1);
        
        // 设置过期时间为7天
        redisService.expire(countKey, 7, TimeUnit.DAYS);
        
        // 更新热门搜索关键词列表
        updateHotKeywords();
        
        return Result.success("搜索关键词记录成功");
    }

    /**
     * 获取用户搜索历史
     * 
     * 获取用户最近的搜索历史记录
     * 
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    @GetMapping("/history/{userId}")
    @Operation(summary = "获取用户搜索历史", description = "获取用户最近的搜索历史记录")
    public Result<List<String>> getUserSearchHistory(@PathVariable Long userId) {
        String historyKey = USER_SEARCH_HISTORY_PREFIX + userId;
        
        // 获取最近20条搜索记录
        List<Object> history = redisService.lGet(historyKey, 0, 19);
        
        // 转换为字符串列表
        List<String> result = new ArrayList<>();
        if (history != null) {
            for (Object item : history) {
                result.add(item.toString());
            }
        }
        
        return Result.success(result);
    }

    /**
     * 获取热门搜索关键词
     * 
     * 获取当前热门的搜索关键词列表
     * 
     * @return 热门搜索关键词列表
     */
    @GetMapping("/hot-keywords")
    @Operation(summary = "获取热门搜索关键词", description = "获取当前热门的搜索关键词列表")
    public Result<List<String>> getHotKeywords() {
        // 尝试从Redis获取缓存
        List<String> hotKeywords = (List<String>) redisService.get(HOT_KEYWORDS_KEY);
        
        if (hotKeywords == null) {
            // 缓存不存在，重新生成
            hotKeywords = updateHotKeywords();
        }
        
        return Result.success(hotKeywords);
    }

    /**
     * 清除用户搜索历史
     * 
     * 清除指定用户的搜索历史记录
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/history/{userId}")
    @Operation(summary = "清除用户搜索历史", description = "清除指定用户的搜索历史记录")
    public Result<String> clearUserSearchHistory(@PathVariable Long userId) {
        String historyKey = USER_SEARCH_HISTORY_PREFIX + userId;
        boolean deleted = redisService.delete(historyKey);
        
        if (deleted) {
            return Result.success("搜索历史清除成功");
        } else {
            return Result.error("搜索历史不存在或清除失败");
        }
    }

    /**
     * 获取关键词搜索次数
     * 
     * 获取指定关键词的搜索次数
     * 
     * @param keyword 搜索关键词
     * @return 搜索次数
     */
    @GetMapping("/count/{keyword}")
    @Operation(summary = "获取关键词搜索次数", description = "获取指定关键词的搜索次数")
    public Result<Long> getKeywordSearchCount(@PathVariable String keyword) {
        String countKey = SEARCH_COUNT_PREFIX + keyword;
        Long count = redisService.get(countKey) != null ? Long.valueOf(redisService.get(countKey).toString()) : 0L;
        
        return Result.success(count);
    }

    /**
     * 搜索失物（带关键词记录）
     * 
     * 根据关键词搜索失物，并记录搜索关键词
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/items")
    @Operation(summary = "搜索失物", description = "根据关键词搜索失物，并记录搜索关键词")
    public Result<List<LostItem>> searchItems(@RequestParam Long userId, @RequestParam String keyword) {
        // 记录搜索关键词
        recordSearchKeyword(userId, keyword);
        
        // 执行搜索
        List<LostItem> items = lostItemService.searchByKeyword(keyword);
        
        return Result.success(items);
    }

    /**
     * 更新热门搜索关键词列表
     * 
     * 从Redis中获取所有关键词的搜索次数，排序后生成热门关键词列表
     * 
     * @return 热门搜索关键词列表
     */
    private List<String> updateHotKeywords() {
        // 获取所有搜索关键词的计数
        Set<String> keys = redisService.keys(SEARCH_COUNT_PREFIX + "*");
        
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取每个关键词的搜索次数
        Map<String, Long> keywordCounts = new HashMap<>();
        for (String key : keys) {
            String keyword = key.substring(SEARCH_COUNT_PREFIX.length());
            Long count = redisService.get(key) != null ? Long.valueOf(redisService.get(key).toString()) : 0L;
            keywordCounts.put(keyword, count);
        }
        
        // 按搜索次数排序，取前10个
        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(keywordCounts.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        List<String> hotKeywords = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
            hotKeywords.add(sortedEntries.get(i).getKey());
        }
        
        // 缓存热门关键词列表，缓存1小时
        redisService.set(HOT_KEYWORDS_KEY, hotKeywords, 1, TimeUnit.HOURS);
        
        return hotKeywords;
    }
}