package com.lostfound.server.config;

import com.lostfound.server.util.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 热门搜索初始化配置
 * 
 * 在应用启动时初始化一些默认的热门搜索关键词
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HotSearchInitializer implements ApplicationRunner {

    private final RedisService redisService;

    // 热门搜索关键词缓存键
    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";
    // 搜索关键词计数键前缀
    private static final String SEARCH_COUNT_PREFIX = "search:count:";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化热门搜索数据...");
        
        // 检查是否已有热门搜索数据
        List<String> existingKeywords = (List<String>) redisService.get(HOT_KEYWORDS_KEY);
        
        if (existingKeywords == null || existingKeywords.isEmpty()) {
            // 添加一些默认的热门搜索关键词
            List<String> defaultKeywords = Arrays.asList(
                "手机", "钥匙", "钱包", "身份证", "学生证", 
                "银行卡", "耳机", "手表", "雨伞", "水杯"
            );
            
            // 为每个关键词设置初始搜索次数
            for (int i = 0; i < defaultKeywords.size(); i++) {
                String keyword = defaultKeywords.get(i);
                // 设置递减的搜索次数，使排序更自然
                long count = 10 - i;
                String countKey = SEARCH_COUNT_PREFIX + keyword;
                redisService.set(countKey, count, 7, TimeUnit.DAYS);
            }
            
            // 生成热门关键词列表
            redisService.set(HOT_KEYWORDS_KEY, defaultKeywords, 1, TimeUnit.HOURS);
            
            log.info("热门搜索数据初始化完成，已添加{}个默认关键词", defaultKeywords.size());
        } else {
            log.info("热门搜索数据已存在，跳过初始化");
        }
    }
}