package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.ClaimRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 认领记录表 Mapper 接口
 * 
 * 对应数据库表：claim_records
 * 
 * 提供失物认领相关的数据访问操作，包括：
 * 1. 基础CRUD操作（继承自BaseMapper）
 * 2. 按条件查询认领记录（物品ID、认领用户ID、创建者ID、状态等）
 * 3. 查询待处理认领记录
 * 4. 统计用户认领记录数量
 * 5. 查询物品的最新认领记录
 * 6. 更新认领记录状态
 *
 * 使用@Select和@Update注解定义SQL语句，简化XML配置
 *
 */
@Mapper
public interface ClaimRecordMapper extends BaseMapper<ClaimRecord> {

    /**
     * 根据物品ID查询认领记录
     * 
     * 查询指定物品的所有认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间或更新时间降序排序，最新的记录排在前面
     * 
     * @param itemId 物品ID，必须是已存在的失物ID
     * @return 指定物品的认领记录列表，包含完整的关联信息，如果没有认领记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.item_id = #{itemId} " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC")
    List<ClaimRecord> selectByItemId(@Param("itemId") Long itemId);

    /**
     * 根据认领用户ID查询认领记录
     * 
     * 查询指定用户提交的所有认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间或更新时间降序排序，最新的记录排在前面
     * 
     * @param claimUserId 认领用户ID，必须是已存在的用户ID
     * @return 指定用户提交的认领记录列表，包含完整的关联信息，如果没有认领记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.claim_user_id = #{claimUserId} " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC")
    List<ClaimRecord> selectByClaimUserId(@Param("claimUserId") Long claimUserId);

    /**
     * 根据物品创建者ID查询认领记录
     * 
     * 查询指定用户创建的所有物品的认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间或更新时间降序排序，最新的记录排在前面
     * 
     * @param creatorId 物品创建者ID，必须是已存在的用户ID
     * @return 指定用户创建的物品的所有认领记录列表，包含完整的关联信息，如果没有认领记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE li.creator_id = #{creatorId} " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC")
    List<ClaimRecord> selectByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 根据状态查询认领记录
     * 
     * 查询指定状态的所有认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间降序排序，最新的记录排在前面
     * 
     * 支持的状态值：
     * - PENDING: 待处理（用户已提交认领申请，等待管理员审核）
     * - APPROVED: 已通过（管理员审核通过，认领成功）
     * - REJECTED: 已拒绝（管理员审核拒绝，认领失败）
     * 
     * @param status 认领记录状态，必须是有效的状态值
     * @return 指定状态的认领记录列表，包含完整的关联信息，如果没有匹配记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.status = #{status} " +
            "ORDER BY cr.created_time DESC")
    List<ClaimRecord> selectByStatus(@Param("status") String status);

    /**
     * 根据状态查询认领记录（包含关联数据）
     * 
     * 查询指定状态的所有认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 与selectByStatus方法功能相同，但排序方式不同，此方法按创建时间或更新时间降序排序
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 
     * 支持的状态值：
     * - PENDING: 待处理（用户已提交认领申请，等待管理员审核）
     * - APPROVED: 已通过（管理员审核通过，认领成功）
     * - REJECTED: 已拒绝（管理员审核拒绝，认领失败）
     * 
     * @param status 认领记录状态，必须是有效的状态值
     * @return 指定状态的认领记录列表，包含完整的关联信息，如果没有匹配记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.status = #{status} " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC")
    List<ClaimRecord> selectByStatusWithDetails(@Param("status") String status);

    /**
     * 查询待处理的认领记录
     * 
     * 查询所有状态为PENDING的认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间升序排序，最早提交的申请排在前面，便于管理员按顺序处理
     * 
     * 通常用于管理员查看待审核的认领申请列表
     * 
     * @return 所有待处理的认领记录列表，包含完整的关联信息，如果没有待处理记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.status = 'PENDING' " +
            "ORDER BY cr.created_time ASC")
    List<ClaimRecord> selectPendingRecords();

    /**
     * 统计用户认领记录数量
     * 
     * 按状态分组统计指定用户的认领记录数量
     * 使用GROUP BY语句按状态分组，返回结果包含状态和对应的数量
     * 返回结果格式示例：
     * - [{status: "PENDING", count: 2}, {status: "APPROVED", count: 3}, {status: "REJECTED", count: 1}]
     * 
     * 支持的状态值：
     * - PENDING: 待处理（用户已提交认领申请，等待管理员审核）
     * - APPROVED: 已通过（管理员审核通过，认领成功）
     * - REJECTED: 已拒绝（管理员审核拒绝，认领失败）
     * 
     * @param userId 用户ID，必须是已存在的用户ID
     * @return 统计结果列表，每个元素包含status和count两个字段，如果没有记录则返回空列表
     */
    @Select("SELECT status, COUNT(*) as count FROM claim_records WHERE claim_user_id = #{userId} GROUP BY status")
    List<java.util.Map<String, Object>> countByUserAndStatus(@Param("userId") Long userId);

    /**
     * 查询物品的最新认领记录
     * 
     * 查询指定物品的最新一条认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间或更新时间降序排序，使用LIMIT 1限制只返回最新的一条记录
     * 
     * 通常用于显示物品的当前认领状态
     * 
     * @param itemId 物品ID，必须是已存在的失物ID
     * @return 物品的最新认领记录，包含完整的关联信息，如果没有认领记录则返回null
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.item_id = #{itemId} " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC " +
            "LIMIT 1")
    ClaimRecord selectLatestByItemId(@Param("itemId") Long itemId);

    /**
     * 分页查询所有认领记录（包含关联数据）
     * 
     * 查询所有认领记录，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 查询结果按创建时间或更新时间降序排序，最新的记录排在前面
     * 
     * 通常用于管理员查看所有认领记录列表，可以配合MyBatis-Plus的分页插件实现分页功能
     * 
     * @return 所有认领记录列表，包含完整的关联信息，如果没有记录则返回空列表
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "ORDER BY COALESCE(cr.created_time, cr.updated_time) DESC")
    List<ClaimRecord> selectAllWithDetails();

    /**
     * 更新认领记录状态
     * 
     * 更新指定认领记录的状态，并自动设置处理时间和更新时间为当前时间
     * 同时更新管理员备注信息，记录审核意见或处理原因
     * 
     * 支持的状态值：
     * - PENDING: 待处理（用户已提交认领申请，等待管理员审核）
     * - APPROVED: 已通过（管理员审核通过，认领成功）
     * - REJECTED: 已拒绝（管理员审核拒绝，认领失败）
     * 
     * 通常用于管理员审核认领申请，通过或拒绝用户的认领请求
     * 
     * @param recordId 认领记录ID，必须是已存在的记录ID
     * @param status 新状态，必须是有效的状态值
     * @param adminRemark 管理员备注，可以是审核意见或处理原因，可为空字符串
     * @return 影响的行数，1表示更新成功，0表示记录ID不存在
     */
    @Update("UPDATE claim_records SET status = #{status}, admin_remark = #{adminRemark}, processed_time = NOW(), updated_time = NOW() WHERE id = #{recordId}")
    int updateRecordStatus(@Param("recordId") Long recordId, @Param("status") String status, @Param("adminRemark") String adminRemark);
    
    /**
     * 根据ID查询认领记录（包含关联数据）
     * 
     * 根据认领记录ID查询详细信息，包含关联的物品信息、认领用户信息、创建者信息和分类信息
     * 关联查询包括：
     * - 物品名称(itemName)从lost_items表获取
     * - 认领用户名(claimUserName)从users表获取
     * - 创建者名(creatorName)从users表获取
     * - 物品分类名称(itemCategory)从item_categories表获取
     * 
     * 通常用于查看认领记录的详细信息，如管理员审核时查看完整信息
     * 
     * @param id 认领记录ID，必须是已存在的记录ID
     * @return 认领记录详细信息，包含完整的关联信息，如果记录不存在则返回null
     */
    @Select("SELECT cr.*, li.item_name as itemName, li.description as itemDescription, li.lost_location as lostLocation, li.lost_time as lostTime, li.item_images as itemImage, cu.username as claimUserName, u.username as creatorName, ic.category_name as itemCategory " +
            "FROM claim_records cr " +
            "LEFT JOIN lost_items li ON cr.item_id = li.id " +
            "LEFT JOIN users cu ON cr.claim_user_id = cu.id " +
            "LEFT JOIN users u ON li.creator_id = u.id " +
            "LEFT JOIN item_categories ic ON li.category_id = ic.id " +
            "WHERE cr.id = #{id}")
    ClaimRecord selectByIdWithDetails(@Param("id") Long id);
}