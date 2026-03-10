package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.LostItem;

import java.util.List;

/**
 * 失物管理服务接口
 * 
 * 定义失物管理的通用方法，避免在多个服务中重复实现
 * 
 * 设计目的：
 * 1. 提取失物管理的公共方法，形成统一的服务接口
 * 2. 支持多种业务场景下的失物管理需求
 * 3. 为不同角色（普通用户、管理员）提供统一的服务规范
 * 
 * 功能范围：
 * - 基础CRUD操作（增删改查）
 * - 分页查询和多条件筛选
 * - 状态管理和统计功能
 * 
 * 使用场景：
 * - 普通用户服务：实现用户发布和查询失物功能
 * - 管理员服务：实现管理员管理所有失物信息功能
 * - 其他业务模块：需要处理失物相关逻辑的地方
 */
public interface LostItemManagementService {
    
    /**
     * 获取失物分页数据
     * 
     * 支持多条件组合查询的分页方法，可按物品名称、分类、状态和丢失地点进行筛选
     * 所有查询条件均为可选，不提供条件时查询所有失物信息
     * 
     * @param pageNum 当前页码，从1开始
     * @param pageSize 每页显示数量
     * @param itemName 物品名称，支持模糊查询（可为空）
     * @param category 物品分类，支持模糊查询（可为空）
     * @param status 失物状态，精确匹配（可为空）
     * @param location 丢失地点，支持模糊查询（可为空）
     * @return 分页查询结果，包含数据列表和分页信息
     */
    Page<LostItem> getLostItemPage(Integer pageNum, Integer pageSize, String itemName, String category, String status, String location);
    
    /**
     * 根据ID获取失物
     * 
     * 获取指定ID的失物详细信息，包括所有字段
     * 用于展示失物的完整信息，如物品名称、描述、图片等
     * 
     * @param id 失物ID
     * @return 失物详细信息，如果不存在则返回null
     */
    LostItem getLostItemById(Long id);
    
    /**
     * 根据状态获取失物列表
     * 
     * 获取指定状态的所有失物信息，支持以下状态：
     * - LOST: 丢失中
     * - FOUND: 已找到
     * - RETURNED: 已归还
     * 
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return 指定状态的失物信息列表
     */
    List<LostItem> getLostItemsByStatus(String status);
    
    /**
     * 保存失物
     * 
     * 保存新的失物信息到数据库
     * 保存时会自动设置创建时间和更新时间
     * 
     * @param lostItem 失物信息对象
     * @return 保存成功返回true，失败返回false
     */
    boolean saveLostItem(LostItem lostItem);
    
    /**
     * 更新失物
     * 
     * 更新指定ID的失物信息，支持部分字段更新
     * 更新时会自动修改更新时间
     * 
     * @param lostItem 包含更新信息的失物对象（必须包含ID）
     * @return 更新成功返回true，失败返回false
     */
    boolean updateLostItem(LostItem lostItem);
    
    /**
     * 根据ID删除失物
     * 
     * 删除指定ID的失物信息，此操作不可恢复
     * 注意：删除前应确认该失物信息是否有关联的认领记录
     * 
     * @param id 要删除的失物ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteLostItemById(Long id);
    
    /**
     * 获取所有失物数量
     * 
     * 统计系统中所有失物记录的总数
     * 用于展示系统规模和数据分析
     * 
     * @return 所有失物记录的总数
     */
    long countLostItems();
    
    /**
     * 根据状态获取失物数量
     * 
     * 统计指定状态的失物记录数量
     * 用于展示各状态失物的分布情况和数据分析
     * 
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return 指定状态的失物记录数量
     */
    long countLostItemsByStatus(String status);
}