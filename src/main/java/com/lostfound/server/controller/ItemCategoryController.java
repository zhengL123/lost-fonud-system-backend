package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.util.Result;
import com.lostfound.server.entity.ItemCategory;
import com.lostfound.server.service.ItemCategoryService;
import com.lostfound.server.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物品分类控制器
 * 提供失物物品分类的管理功能，包括：
 * 1. 分类创建、查询、更新、删除
 * 2. 分类列表获取（按排序字段排序）
 * 3. 按名称查询分类
 * 4. 分类搜索功能
 * 
 * 分类用于对失物物品进行归类管理，便于用户查找和管理
 * 常见分类如：电子产品、证件、生活用品、书籍资料等
 * 
 * @author 系统开发团队
 * @version 1.0
 * @since 2023-01-01
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ItemCategoryController {

    // 注入物品分类服务层，处理分类相关业务逻辑
    private final ItemCategoryService itemCategoryService;

    /**
     * 创建分类
     * 
     * 创建新的物品分类，分类名称不能重复
     * 创建成功后，分类可用于失物物品的分类管理
     * 
     * @param category 分类信息对象，包含分类名称、描述、排序等字段
     * @return Result<String> 创建成功返回"创建成功"，失败返回错误信息
     */
    @PostMapping
    public Result<String> createCategory(@RequestBody ItemCategory category) {
        // 检查分类名是否已存在
        ItemCategory existing = itemCategoryService.getCategoryByName(category.getCategoryName());
        if (existing != null) {
            return Result.error("分类名称已存在");
        }

        boolean saved = itemCategoryService.save(category);
        return saved ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 分页查询分类
     * 
     * 分页获取物品分类列表，支持按分类名称进行筛选
     * 分页结果按照排序字段升序排列
     * 
     * @param current 当前页码，默认为1
     * @param size 每页条数，默认为10
     * @param categoryName 分类名称筛选，可选参数
     * @return Result<PageResult<ItemCategory>> 分页结果，包含分类列表和分页信息
     */
    @GetMapping("/page")
    public Result<PageResult<ItemCategory>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String categoryName) {
        
        Page<ItemCategory> page = new Page<>(current, size);
        Page<ItemCategory> categoryPage = itemCategoryService.getCategoryPage(page, categoryName);
        // 转换为自定义分页响应格式
        PageResult<ItemCategory> pageResult = PageResult.of(categoryPage);
        return Result.success(pageResult);
    }

    /**
     * 查询所有分类（按排序）
     * 
     * 获取所有物品分类列表，按照排序字段升序排列
     * 常用于前端下拉框展示分类选项
     * 
     * @return Result<List<ItemCategory>> 包含所有分类信息的列表
     */
    @GetMapping("/all")
    public Result<List<ItemCategory>> getAllCategories() {
        List<ItemCategory> categories = itemCategoryService.getAllCategoriesOrdered();
        return Result.success(categories);
    }

    /**
     * 根据ID查询分类
     * 
     * 通过分类ID获取单个分类的详细信息
     * 用于分类编辑或详情展示
     * 
     * @param id 分类ID
     * @return Result<ItemCategory> 返回分类信息，不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public Result<ItemCategory> getById(@PathVariable Long id) {
        ItemCategory category = itemCategoryService.getById(id);
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }

    /**
     * 更新分类信息
     * 
     * 更新指定ID的分类信息，支持修改名称、描述、排序等字段
     * 注意：修改分类名称时需要确保不与现有分类重复
     * 
     * @param id 要更新的分类ID
     * @param category 包含更新信息的分类对象
     * @return Result<String> 更新成功返回"更新成功"，失败返回错误信息
     */
    @PutMapping("/{id}")
    public Result<String> updateById(@PathVariable Long id, @RequestBody ItemCategory category) {
        category.setId(id);
        boolean updated = itemCategoryService.updateById(category);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除分类
     * 
     * 删除指定的分类，删除前需要检查是否有物品使用该分类
     * 如果有物品使用该分类，则不允许删除
     * 
     * @param id 分类ID
     * @return Result<String> 删除成功返回"删除成功"，失败返回错误信息
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        // 检查分类是否存在
        ItemCategory category = itemCategoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }

        // 检查是否有物品使用该分类
        boolean hasItems = itemCategoryService.hasItemsInCategory(id);
        if (hasItems) {
            return Result.error("该分类下有物品，不能删除");
        }

        boolean deleted = itemCategoryService.removeById(id);
        return deleted ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据名称查询分类
     * 
     * 通过分类名称精确查询分类信息
     * 常用于创建分类前检查名称是否已存在
     * 
     * @param categoryName 分类名称
     * @return Result<ItemCategory> 返回分类信息，不存在则返回错误信息
     */
    @GetMapping("/name/{categoryName}")
    public Result<ItemCategory> getByName(@PathVariable String categoryName) {
        ItemCategory category = itemCategoryService.getCategoryByName(categoryName);
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }

    /**
     * 搜索分类
     * 
     * 根据关键词搜索分类，支持按名称和描述进行模糊匹配
     * 当前实现返回所有分类，实际应实现关键词搜索功能
     * 
     * @param keyword 搜索关键词
     * @return Result<List<ItemCategory>> 包含匹配分类信息的列表
     */
    @GetMapping("/search")
    public Result<List<ItemCategory>> searchCategories(@RequestParam String keyword) {
        // 这里可以调用Mapper中的搜索方法
        List<ItemCategory> categories = itemCategoryService.list();
        return Result.success(categories);
    }

    /**
     * 获取分类及其物品数量
     * 
     * 获取所有分类以及每个分类下的物品数量
     * 用于分类管理页面展示分类统计信息
     * 
     * @return Result<List<Map<String, Object>>> 包含分类信息和物品数量的列表
     */
    @GetMapping("/with-count")
    public Result<List<Map<String, Object>>> getCategoriesWithItemCount() {
        List<Map<String, Object>> categoriesWithCount = itemCategoryService.getCategoriesWithItemCount();
        return Result.success(categoriesWithCount);
    }
}