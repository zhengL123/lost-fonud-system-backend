package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.ItemCategory;
import com.lostfound.server.entity.LostItem;
import com.lostfound.server.mapper.ItemCategoryMapper;
import com.lostfound.server.service.ItemCategoryService;
import com.lostfound.server.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品分类服务实现类
 * 
 * 实现物品分类相关的业务逻辑，包括：
 * 1. 基础CRUD操作（继承自ServiceImpl）
 * 2. 按名称查询分类信息
 * 3. 获取排序后的分类列表
 * 
 * 实现特点：
 * - 使用LambdaQueryWrapper构建查询条件，确保类型安全
 * - 对输入参数进行非空和有效性验证
 * - 支持多字段排序，提高查询灵活性
 * 
 * 物品分类用于对失物进行归类管理，方便用户查找和发布信息
 */
@Service
public class ItemCategoryServiceImpl extends ServiceImpl<ItemCategoryMapper, ItemCategory> implements ItemCategoryService {

    @Autowired
    private LostItemService lostItemService;

    /**
     * 分页查询分类
     * 
     * 实现逻辑：
     * 1. 创建Lambda查询条件构造器
     * 2. 如果分类名称不为空，添加模糊查询条件
     * 3. 添加排序条件：先按sort_order升序，再按created_time降序
     * 4. 执行分页查询并返回结果
     * 
     * @param page 分页参数
     * @param categoryName 分类名称筛选，可选参数
     * @return 分页结果
     */
    @Override
    public Page<ItemCategory> getCategoryPage(Page<ItemCategory> page, String categoryName) {
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<ItemCategory> wrapper = new LambdaQueryWrapper<>();
        
        // 如果分类名称不为空，添加模糊查询条件
        if (StringUtils.hasText(categoryName)) {
            wrapper.like(ItemCategory::getCategoryName, categoryName.trim());
        }
        
        // 添加排序条件：先按sort_order升序，再按created_time降序
        wrapper.orderByAsc(ItemCategory::getSortOrder)
               .orderByDesc(ItemCategory::getCreatedTime);
        
        // 执行分页查询并返回结果
        return page(page, wrapper);
    }
    
    /**
     * 根据分类名称查询分类
     * 
     * 实现逻辑：
     * 1. 验证分类名称是否为空或空白字符串
     * 2. 使用LambdaQueryWrapper构建查询条件
     * 3. 按分类名称进行精确匹配查询
     * 4. 返回查询结果（唯一记录或null）
     * 
     * 注意事项：
     * - 分类名称会去除前后空格后再进行匹配
     * - 如果存在多个同名分类，只返回第一个匹配结果
     * 
     * @param categoryName 分类名称，不能为空或空白字符串
     * @return 分类信息，如果不存在则返回null
     */
    @Override
    public ItemCategory getCategoryByName(String categoryName) {
        // 验证分类名称是否为空或空白字符串
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<ItemCategory> wrapper = new LambdaQueryWrapper<>();
        // 添加分类名称精确匹配条件，去除前后空格
        wrapper.eq(ItemCategory::getCategoryName, categoryName.trim());
        // 执行查询并返回唯一结果
        return getOne(wrapper);
    }

    /**
     * 获取所有分类并按排序字段排序
     * 
     * 实现逻辑：
     * 1. 使用LambdaQueryWrapper构建查询条件
     * 2. 首先按sort_order升序排序
     * 3. 然后按created_time降序排序（作为次要排序条件）
     * 4. 返回排序后的分类列表
     * 
     * 排序规则：
     * - 主要排序：按sort_order升序（从小到大）
     * - 次要排序：按created_time降序（从新到旧）
     * 
     * 使用场景：
     * - 前端展示分类列表时，确保按预设顺序显示
     * - 新增分类时，如果sort_order相同，则新创建的分类排在前面
     * 
     * @return 按sort_order升序、created_time降序排列的分类列表
     */
    @Override
    public List<ItemCategory> getAllCategoriesOrdered() {
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<ItemCategory> wrapper = new LambdaQueryWrapper<>();
        // 添加排序条件：先按sort_order升序，再按created_time降序
        wrapper.orderByAsc(ItemCategory::getSortOrder)
               .orderByDesc(ItemCategory::getCreatedTime);
        // 执行查询并返回排序后的列表
        return list(wrapper);
    }
    
    /**
     * 检查分类下是否有物品
     * 
     * 实现逻辑：
     * 1. 验证分类ID是否为空
     * 2. 使用LostItemService查询该分类下是否有物品
     * 3. 返回查询结果
     * 
     * @param categoryId 分类ID
     * @return true表示分类下有物品，false表示分类下没有物品
     */
    @Override
    public boolean hasItemsInCategory(Long categoryId) {
        // 验证分类ID是否为空
        if (categoryId == null) {
            return false;
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();
        // 添加分类ID匹配条件
        wrapper.eq(LostItem::getCategoryId, categoryId);
        // 查询该分类下的物品数量
        long count = lostItemService.count(wrapper);
        // 如果数量大于0，表示分类下有物品
        return count > 0;
    }
    
    /**
     * 获取分类及其物品数量
     * 
     * 实现逻辑：
     * 1. 获取所有分类
     * 2. 对每个分类，查询该分类下的物品数量
     * 3. 将分类信息和物品数量组装成Map
     * 4. 返回结果列表
     * 
     * @return 包含分类信息和物品数量的列表
     */
    @Override
    public List<Map<String, Object>> getCategoriesWithItemCount() {
        // 获取所有分类
        List<ItemCategory> categories = getAllCategoriesOrdered();
        // 创建结果列表
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 遍历每个分类
        for (ItemCategory category : categories) {
            // 创建Map存储分类信息和物品数量
            Map<String, Object> categoryMap = new HashMap<>();
            // 添加分类信息
            categoryMap.put("id", category.getId());
            categoryMap.put("categoryName", category.getCategoryName());
            categoryMap.put("description", category.getDescription());
            categoryMap.put("sortOrder", category.getSortOrder());
            categoryMap.put("createdTime", category.getCreatedTime());
            
            // 查询该分类下的物品数量
            LambdaQueryWrapper<LostItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LostItem::getCategoryId, category.getId());
            long itemCount = lostItemService.count(wrapper);
            
            // 添加物品数量
            categoryMap.put("itemCount", itemCount);
            
            // 将Map添加到结果列表
            result.add(categoryMap);
        }
        
        return result;
    }
}