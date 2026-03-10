package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.LostItem;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 失物招领服务接口
 * 
 * 定义失物招领相关的业务操作方法，包括：
 * 1. 基础CRUD操作（增删改查）
 * 2. 条件查询（按状态、分类、发布者等）
 * 3. 分页查询（支持多条件筛选）
 * 4. 模糊查询（按物品名称等）
 * 
 * 继承IService接口，获得MyBatis-Plus提供的基础CRUD方法
 */
public interface LostItemService extends IService<LostItem> {

    /**
     * 查询所有失物列表
     * 
     * 获取数据库中所有的失物信息，不进行分页处理
     * 注意：数据量大时可能影响性能，建议使用分页查询
     * 
     * @return 所有失物信息的列表
     */
    @Cacheable(value = "lostItems", key = "'all'")
    List<LostItem> list();

    /**
     * 分页查询失物列表
     * 
     * 支持多条件组合查询的分页方法，可按物品名称、丢失地点和状态进行筛选
     * 默认按创建时间降序排序，最新发布的失物信息排在前面
     * 
     * @param page 分页对象，包含页码和每页数量等信息
     * @param itemName 物品名称，支持模糊查询（可为空）
     * @param lostLocation 丢失地点，支持模糊查询（可为空）
     * @param status 失物状态，精确匹配（可为空）
     * @return 分页查询结果，包含数据列表和分页信息
     */
    Page<LostItem> page(Page<LostItem> page, String itemName, String lostLocation, String status);

    /**
     * 根据状态查询失物列表
     * 
     * 获取指定状态的所有失物信息，支持以下状态：
     * - LOST: 丢失中
     * - FOUND: 已找到
     * - RETURNED: 已归还
     * 
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return 指定状态的失物信息列表
     */
    @Cacheable(value = "lostItems", key = "'status:' + #status")
    List<LostItem> getItemsByStatus(String status);

    /**
     * 根据物品名称模糊查询
     * 
     * 支持按物品名称进行模糊查询，不区分大小写
     * 可用于搜索特定类型的物品，如"钱包"、"手机"等
     * 
     * @param itemName 物品名称或关键词
     * @return 包含指定关键词的失物信息列表
     */
    @Cacheable(value = "lostItems", key = "'name:' + #itemName")
    List<LostItem> searchByItemName(String itemName);

    /**
     * 根据分类ID查询失物
     * 
     * 获取指定分类下的所有失物信息
     * 分类ID对应物品分类表中的主键，如：
     * - 1: 电子产品
     * - 2: 生活用品
     * - 3: 学习用品
     * 
     * @param categoryId 物品分类ID
     * @return 指定分类下的失物信息列表
     */
    @Cacheable(value = "lostItems", key = "'category:' + #categoryId")
    List<LostItem> getItemsByCategoryId(Long categoryId);

    /**
     * 根据发布者ID查询失物
     * 
     * 获取指定用户发布的所有失物信息
     * 可用于用户中心查看自己发布的失物信息
     * 
     * @param creatorId 发布者ID（用户ID）
     * @return 指定用户发布的失物信息列表
     */
    @Cacheable(value = "lostItems", key = "'creator:' + #creatorId")
    List<LostItem> getItemsByCreatorId(Long creatorId);

    /**
     * 根据ID查询失物详情
     * 
     * 获取指定ID的失物详细信息，包括所有字段
     * 包含关联查询的创建者姓名和分类名称
     * 
     * @param id 失物ID
     * @return 失物详细信息，如果不存在则返回null
     */
    @Cacheable(value = "lostItems", key = "#id")
    LostItem getById(Long id);

    /**
     * 新增失物
     * 
     * 保存新的失物信息到数据库
     * 保存时会自动设置创建时间和更新时间
     * 
     * @param lostItem 失物信息对象
     * @return 保存成功返回true，失败返回false
     */
    @CacheEvict(value = "lostItems", allEntries = true)
    boolean save(LostItem lostItem);

    /**
     * 根据ID修改失物
     * 
     * 更新指定ID的失物信息，支持部分字段更新
     * 更新时会自动修改更新时间
     * 
     * @param lostItem 包含更新信息的失物对象（必须包含ID）
     * @return 更新成功返回true，失败返回false
     */
    @CacheEvict(value = "lostItems", allEntries = true)
    boolean updateById(LostItem lostItem);

    /**
     * 根据ID删除失物
     * 
     * 删除指定ID的失物信息，此操作不可恢复
     * 注意：删除前应确认该失物信息是否有关联的认领记录
     * 
     * @param id 要删除的失物ID
     * @return 删除成功返回true，失败返回false
     */
    @CacheEvict(value = "lostItems", allEntries = true)
    boolean removeById(Long id);

    /**
     * 根据关键词搜索失物
     * 
     * 支持在物品名称、描述和丢失地点中进行关键词搜索
     * 不区分大小写，支持模糊匹配
     * 
     * @param keyword 搜索关键词
     * @return 包含关键词的失物信息列表
     */
    List<LostItem> searchByKeyword(String keyword);
    
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
    Page<LostItem> pageWithCategoryName(Page<LostItem> page, String itemName, Long categoryId, String status, String location);

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
    boolean updateItemStatus(Long itemId, String status);
}