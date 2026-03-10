package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.LostItem;
import com.lostfound.server.mapper.LostItemMapper;
import com.lostfound.server.service.LostItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 失物招领服务实现类
 * 
 * 实现失物招领相关的业务逻辑，包括：
 * 1. 基础CRUD操作（增删改查）
 * 2. 条件查询（按状态、分类、发布者等）
 * 3. 分页查询（支持多条件筛选）
 * 4. 模糊查询（按物品名称等）
 * 
 * 继承ServiceImpl类，获得MyBatis-Plus提供的基础CRUD方法实现
 */
@Service
@RequiredArgsConstructor
public class LostItemServiceImpl extends ServiceImpl<LostItemMapper, LostItem> implements LostItemService {

    // 注入失物数据访问层，用于数据库操作
    private final LostItemMapper lostItemMapper;

    /**
     * 查询所有失物列表
     * 
     * 获取数据库中所有的失物信息，按创建时间降序排序
     * 使用LambdaQueryWrapper构建查询条件，确保代码类型安全
     * 
     * @return 按创建时间降序排列的所有失物信息列表
     */
    @Override
    public List<LostItem> list() {
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        // 按创建时间降序排序，最新发布的失物信息排在前面
        queryWrapper.orderByDesc(LostItem::getCreatedTime);
        // 执行查询并返回结果
        return list(queryWrapper);
    }

    /**
     * 分页查询失物列表
     * 
     * 支持多条件组合查询的分页方法，可按物品名称、丢失地点和状态进行筛选
     * 使用LambdaQueryWrapper构建动态查询条件，根据参数是否为空决定是否添加条件
     * 所有查询结果按创建时间降序排序
     * 
     * @param page 分页对象，包含页码和每页数量等信息
     * @param itemName 物品名称，支持模糊查询（可为空）
     * @param lostLocation 丢失地点，支持模糊查询（可为空）
     * @param status 失物状态，精确匹配（可为空）
     * @return 分页查询结果，包含数据列表和分页信息
     */
    @Override
    public Page<LostItem> page(Page<LostItem> page, String itemName, String lostLocation, String status) {
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();

        // 如果物品名称不为空，添加模糊查询条件
        if (StringUtils.hasText(itemName)) {
            queryWrapper.like(LostItem::getItemName, itemName.trim());
        }

        // 如果丢失地点不为空，添加模糊查询条件
        if (StringUtils.hasText(lostLocation)) {
            queryWrapper.like(LostItem::getLostLocation, lostLocation.trim());
        }

        // 如果状态不为空，添加精确匹配条件
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(LostItem::getStatus, status.trim());
        }

        // 按创建时间降序排序
        queryWrapper.orderByDesc(LostItem::getCreatedTime);

        // 执行分页查询并返回结果
        return super.page(page, queryWrapper);
    }

    /**
     * 根据状态查询失物列表
     * 
     * 获取指定状态的所有失物信息，支持以下状态：
     * - LOST: 丢失中
     * - FOUND: 已找到
     * - RETURNED: 已归还
     * 
     * 如果状态为空，则返回所有失物信息
     * 查询结果按创建时间降序排序
     * 
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return 指定状态的失物信息列表，按创建时间降序排列
     */
    @Override
    public List<LostItem> getItemsByStatus(String status) {
        // 如果状态为空，返回所有失物信息
        if (!StringUtils.hasText(status)) {
            return list();
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        // 添加状态精确匹配条件
        queryWrapper.eq(LostItem::getStatus, status.trim())
                // 按创建时间降序排序
                .orderByDesc(LostItem::getCreatedTime);
        // 执行查询并返回结果
        return list(queryWrapper);
    }

    /**
     * 根据物品名称模糊查询
     * 
     * 支持按物品名称进行模糊查询，不区分大小写
     * 可用于搜索特定类型的物品，如"钱包"、"手机"等
     * 
     * 如果物品名称为空，则返回所有失物信息
     * 查询结果按创建时间降序排序
     * 
     * @param itemName 物品名称或关键词
     * @return 包含指定关键词的失物信息列表，按创建时间降序排列
     */
    @Override
    public List<LostItem> searchByItemName(String itemName) {
        // 如果物品名称为空，返回所有失物信息
        if (!StringUtils.hasText(itemName)) {
            return list();
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        // 添加物品名称模糊查询条件
        queryWrapper.like(LostItem::getItemName, itemName.trim())
                // 按创建时间降序排序
                .orderByDesc(LostItem::getCreatedTime);
        // 执行查询并返回结果
        return list(queryWrapper);
    }

    /**
     * 根据分类ID查询失物列表
     * 
     * 获取指定分类下的所有失物信息，可用于按类别筛选物品
     * 分类ID对应物品的分类，如"电子产品"、"证件"、"日用品"等
     * 
     * 如果分类ID为空，则返回所有失物信息
     * 查询结果按创建时间降序排序
     * 
     * @param categoryId 分类ID
     * @return 指定分类的失物信息列表，按创建时间降序排列
     */
    @Override
    public List<LostItem> getItemsByCategoryId(Long categoryId) {
        // 如果分类ID为空，返回所有失物信息
        if (categoryId == null) {
            return list();
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        // 添加分类ID精确匹配条件
        queryWrapper.eq(LostItem::getCategoryId, categoryId)
                // 按创建时间降序排序
                .orderByDesc(LostItem::getCreatedTime);
        // 执行查询并返回结果
        return list(queryWrapper);
    }

    /**
     * 根据发布者ID查询失物列表
     * 
     * 获取指定用户发布的所有失物信息，可用于查看个人发布的失物
     * 支持用户查看自己发布的所有失物信息，包括不同状态的记录
     * 
     * 如果发布者ID为空，则返回所有失物信息
     * 查询结果按创建时间降序排序
     * 
     * @param creatorId 发布者ID
     * @return 指定用户发布的失物信息列表，按创建时间降序排列
     */
    @Override
    public List<LostItem> getItemsByCreatorId(Long creatorId) {
        // 如果发布者ID为空，返回所有失物信息
        if (creatorId == null) {
            return list();
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        // 添加发布者ID精确匹配条件
        queryWrapper.eq(LostItem::getCreatorId, creatorId)
                // 按创建时间降序排序
                .orderByDesc(LostItem::getCreatedTime);
        // 执行查询并返回结果
        return list(queryWrapper);
    }

    /**
     * 根据ID查询失物详情
     * 
     * 获取指定ID的失物详细信息，包括所有字段
     * 用于展示失物的完整信息，如物品名称、描述、图片等
     * 
     * @param id 失物ID
     * @return 失物详细信息，如果不存在则返回null
     */
    @Override
    public LostItem getById(Long id) {
        return id == null ? null : super.getById(id);
    }

    /**
     * 保存失物信息
     * 
     * 将新的失物信息保存到数据库中
     * 自动设置创建时间和更新时间为当前时间
     * 
     * @param lostItem 失物实体对象
     * @return 保存成功返回true，失败返回false
     */
    @Override
    public boolean save(LostItem lostItem) {
        return lostItem != null && super.save(lostItem);
    }

    /**
     * 根据ID修改失物信息
     * 
     * 更新指定ID的失物信息，只更新传入的非空字段
     * 自动更新更新时间为当前时间
     * 
     * @param lostItem 失物实体对象，包含ID和需要更新的字段
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateById(LostItem lostItem) {
        return lostItem != null && lostItem.getId() != null && super.updateById(lostItem);
    }

    /**
     * 根据ID删除失物信息
     * 
     * 删除指定ID的失物记录，此操作不可恢复
     * 请谨慎使用此方法，建议在实际应用中增加权限验证
     * 
     * @param id 失物ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean removeById(Long id) {
        // 调用父类方法删除失物信息
        return id != null && super.removeById(id);
    }

    /**
     * 根据关键词搜索失物
     * 
     * 支持在物品名称、描述和丢失地点中进行关键词搜索
     * 不区分大小写，支持模糊匹配
     * 
     * @param keyword 搜索关键词
     * @return 包含关键词的失物信息列表
     */
    @Override
    public List<LostItem> searchByKeyword(String keyword) {
        // 如果关键词为空，返回空列表
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<LostItem> queryWrapper = new LambdaQueryWrapper<>();
        
        // 在物品名称、描述和丢失地点中进行模糊查询
        String trimmedKeyword = keyword.trim();
        queryWrapper.and(wrapper -> 
            wrapper.like(LostItem::getItemName, trimmedKeyword)
                   .or()
                   .like(LostItem::getDescription, trimmedKeyword)
                   .or()
                   .like(LostItem::getLostLocation, trimmedKeyword)
        );
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(LostItem::getCreatedTime);
        
        // 执行查询并返回结果
        return list(queryWrapper);
    }
    
    /**
     * 分页查询失物信息（包含类别名称）
     * 
     * 支持多条件组合查询：物品名称、类别ID、状态、丢失地点
     * 使用自定义SQL查询，通过LEFT JOIN关联获取类别名称
     * 查询结果按创建时间降序排序，最新发布的失物信息排在前面
     * 
     * @param page 分页参数（当前页码、每页记录数）
     * @param itemName 物品名称（可选，模糊匹配）
     * @param categoryId 类别ID（可选，精确匹配）
     * @param status 状态（可选，精确匹配）
     * @param location 丢失地点（可选，模糊匹配）
     * @return 分页结果对象，包含查询结果列表和分页信息
     */
    public Page<LostItem> pageWithCategoryName(Page<LostItem> page, String itemName, Long categoryId, String status, String location) {
        // 使用自定义查询获取包含类别名称的数据
        List<LostItem> records = lostItemMapper.selectWithCategoryName(itemName, categoryId, status, location);
        
        // 手动分页处理
        int total = records.size();
        int pageNum = (int) page.getCurrent();
        int pageSize = (int) page.getSize();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        
        List<LostItem> pageRecords = new ArrayList<>();
        if (startIndex < total) {
            pageRecords = records.subList(startIndex, endIndex);
        }
        
        // 设置分页结果
        page.setRecords(pageRecords);
        page.setTotal(total);
        
        return page;
    }

    /**
     * 更新物品状态
     * 
     * 更新指定失物的状态，并自动更新修改时间为当前时间
     * 常用于状态流转，如从"LOST"更新为"FOUND"，或从"FOUND"更新为"RETURNED"
     * 
     * @param itemId 失物ID
     * @param status 新状态（LOST/FOUND/RETURNED）
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateItemStatus(Long itemId, String status) {
        if (itemId == null || !StringUtils.hasText(status)) {
            return false;
        }
        
        int result = lostItemMapper.updateStatus(itemId, status.trim());
        return result > 0;
    }
}