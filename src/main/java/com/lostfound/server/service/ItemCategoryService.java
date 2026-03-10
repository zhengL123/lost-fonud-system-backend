package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.ItemCategory;
import java.util.List;
import java.util.Map;

/**
 * 物品分类服务接口
 * 
 * 定义物品分类相关的业务操作方法，包括：
 * 1. 基础CRUD操作（继承自IService接口）
 * 2. 按名称查询分类信息
 * 3. 获取排序后的分类列表
 * 4. 检查分类下是否有物品
 * 5. 获取分类及其物品数量
 * 
 * 物品分类用于对失物进行归类管理，如：
 * - 电子产品类（手机、电脑等）
 * - 证件类（身份证、学生证等）
 * - 生活用品类（钱包、钥匙等）
 * 
 * 通过分类管理，用户可以更方便地查找和发布失物信息
 */
public interface ItemCategoryService extends IService<ItemCategory> {

    /**
     * 分页查询分类
     * 
     * 分页获取物品分类列表，支持按分类名称进行筛选
     * 分页结果按照排序字段升序排列
     * 
     * @param page 分页参数
     * @param categoryName 分类名称筛选，可选参数
     * @return 分页结果
     */
    Page<ItemCategory> getCategoryPage(Page<ItemCategory> page, String categoryName);
    
    /**
     * 根据分类名称查询分类
     * 
     * 用于获取指定名称的分类信息，支持精确匹配
     * 可用于验证分类名称是否已存在，避免重复创建
     * 
     * @param categoryName 分类名称，不能为空
     * @return 分类信息，如果不存在则返回null
     */
    ItemCategory getCategoryByName(String categoryName);

    /**
     * 获取所有分类并按排序字段排序
     * 
     * 获取系统中所有物品分类，并按照sort_order字段升序排列
     * 用于前端展示分类列表，确保分类按照预设顺序显示
     * 
     * @return 按sort_order字段排序的分类列表
     */
    List<ItemCategory> getAllCategoriesOrdered();
    
    /**
     * 检查分类下是否有物品
     * 
     * 用于删除分类前的检查，确保分类下没有关联的物品
     * 如果分类下有物品，则不允许删除该分类
     * 
     * @param categoryId 分类ID
     * @return true表示分类下有物品，false表示分类下没有物品
     */
    boolean hasItemsInCategory(Long categoryId);
    
    /**
     * 获取分类及其物品数量
     * 
     * 获取所有分类以及每个分类下的物品数量
     * 用于分类管理页面展示分类统计信息
     * 
     * @return 包含分类信息和物品数量的列表
     */
    List<Map<String, Object>> getCategoriesWithItemCount();
}