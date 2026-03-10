package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.LostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 失物信息表 Mapper 接口
 * 对应数据库表：lost_items
 * 提供失物招领相关的数据访问操作，包括：
 * 1. 基础CRUD操作（继承自BaseMapper）
 * 2. 按条件查询失物信息（物品名称、分类、状态、发布者、地点等）
 * 3. 按时间范围查询失物信息
 * 4. 统计各状态失物数量
 * 5. 更新失物状态
 */
@Mapper
public interface LostItemMapper extends BaseMapper<LostItem> {

    /**
     * 根据物品名称模糊查询
     * 使用LIKE语句进行模糊匹配，查询包含指定关键词的失物信息
     * 查询结果按创建时间降序排序，最新发布的失物信息排在前面
     * @param itemName 物品名称或关键词，如"钱包"、"手机"等
     * @return 包含指定关键词的失物信息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE item_name LIKE CONCAT('%', #{itemName}, '%') ORDER BY created_time DESC")
    List<LostItem> selectByItemName(@Param("itemName") String itemName);

    /**
     * 根据分类ID查询失物
     * 精确匹配分类ID，查询指定分类下的所有失物信息
     * 分类ID对应物品分类表中的主键，如：
     * - 1: 电子产品
     * - 2: 生活用品
     * - 3: 学习用品
     * 查询结果按创建时间降序排序
     * @param categoryId 物品分类ID
     * @return 指定分类下的失物信息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE category_id = #{categoryId} ORDER BY created_time DESC")
    List<LostItem> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据状态查询失物
     * 精确匹配状态，查询指定状态的所有失物信息
     * 支持以下状态：
     * - LOST: 丢失中
     * - FOUND: 已找到
     * - RETURNED: 已归还
     * 查询结果按创建时间降序排序
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return 指定状态的失物信息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE status = #{status} ORDER BY created_time DESC")
    List<LostItem> selectByStatus(@Param("status") String status);
    /**
     * 根据发布者ID查询失物
     * 
     * 精确匹配发布者ID，查询指定用户发布的所有失物信息
     * 支持用户查看自己发布的所有失物信息，包括不同状态的记录
     * 查询结果按创建时间降序排序
     * 
     * @param creatorId 发布者ID（用户ID）
     * @return 指定用户发布的失物信息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE creator_id = #{creatorId} ORDER BY created_time DESC")
    List<LostItem> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 根据地点查询失物
     * 
     * 使用LIKE语句进行模糊匹配，查询在指定地点丢失的失物信息
     * 支持模糊查询，如"图书馆"可以匹配"图书馆三楼"、"图书馆二楼"等
     * 查询结果按创建时间降序排序
     * 
     * @param location 丢失地点或关键词，如"图书馆"、"食堂"等
     * @return 在指定地点丢失的失物信息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE lost_location LIKE CONCAT('%', #{location}, '%') ORDER BY created_time DESC")
    List<LostItem> selectByLocation(@Param("location") String location);

    /**
     * 查询指定时间范围内的失物
     * 
     * 查询在指定时间范围内丢失的失物信息
     * 使用BETWEEN语句进行时间范围查询，包含开始时间和结束时间
     * 查询结果按丢失时间降序排序，最新丢失的物品排在前面
     * 
     * @param startTime 开始时间（包含）
     * @param endTime 结束时间（包含）
     * @return 在指定时间范围内丢失的失物信息列表，按丢失时间降序排列
     */
    @Select("SELECT * FROM lost_items WHERE lost_time BETWEEN #{startTime} AND #{endTime} ORDER BY lost_time DESC")
    List<LostItem> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态失物数量
     * 
     * 使用GROUP BY语句按状态分组统计失物数量
     * 返回结果包含状态和对应的数量，如：
     * - {status: "LOST", count: 10}
     * - {status: "FOUND", count: 5}
     * - {status: "RETURNED", count: 3}
     * 
     * @return 状态统计列表，每个元素包含status和count两个字段
     */
    @Select("SELECT status, COUNT(*) as count FROM lost_items GROUP BY status")
    List<java.util.Map<String, Object>> countByStatusGroup();

    /**
     * 更新物品状态
     * 
     * 更新指定失物的状态，并自动更新修改时间为当前时间
     * 常用于状态流转，如从"LOST"更新为"FOUND"，或从"FOUND"更新为"RETURNED"
     * 
     * @param itemId 失物ID
     * @param status 新状态（LOST/FOUND/RETURNED）
     * @return 影响的行数，1表示更新成功，0表示更新失败
     */
    @Update("UPDATE lost_items SET status = #{status}, updated_time = NOW() WHERE id = #{itemId}")
    int updateStatus(@Param("itemId") Long itemId, @Param("status") String status);
    
    /**
     * 分页查询失物信息（包含类别名称）
     * 
     * 使用LEFT JOIN关联查询失物信息和类别信息，获取包含类别名称的失物列表
     * 支持多条件组合查询：物品名称、类别ID、状态、丢失地点
     * 查询结果按创建时间降序排序，最新发布的失物信息排在前面
     * 
     * @param itemName 物品名称（可选，模糊匹配）
     * @param categoryId 类别ID（可选，精确匹配）
     * @param status 状态（可选，精确匹配）
     * @param location 丢失地点（可选，模糊匹配）
     * @return 包含类别名称的失物信息列表
     */
    @Select("SELECT li.*, ic.category_name as categoryName FROM lost_items li " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE 1=1 " +
            "AND (#{itemName} IS NULL OR #{itemName} = '' OR li.item_name LIKE CONCAT('%', #{itemName}, '%')) " +
            "AND (#{categoryId} IS NULL OR li.category_id = #{categoryId}) " +
            "AND (#{status} IS NULL OR #{status} = '' OR li.status = #{status}) " +
            "AND (#{location} IS NULL OR #{location} = '' OR li.lost_location LIKE CONCAT('%', #{location}, '%')) " +
            "ORDER BY li.created_time DESC")
    List<LostItem> selectWithCategoryName(@Param("itemName") String itemName, 
                                        @Param("categoryId") Long categoryId, 
                                        @Param("status") String status, 
                                        @Param("location") String location);
}