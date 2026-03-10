package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.ItemCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 物品分类表 Mapper 接口
 对应数据库表：item_categories
 提供物品分类相关的数据访问操作，包括：
 1. 基础CRUD操作（继承自BaseMapper）
 2. 按名称查询分类信息（精确匹配和模糊匹配）
 3. 按排序字段查询分类列表
 4. 分类排序管理（获取最大排序值、更新排序）
 使用@Select注解定义SQL查询语句，简化XML配置
 */
@Mapper
public interface ItemCategoryMapper extends BaseMapper<ItemCategory> {

    /**
      根据分类名称查询分类信息
      精确匹配分类名称，查询指定分类的详细信息
      通常用于分类名称唯一性验证和分类信息查询
      @param categoryName 分类名称，需完全匹配，不区分大小写（取决于数据库配置）
      @return 分类信息对象，如果不存在则返回null
     */
    @Select("SELECT * FROM item_categories WHERE category_name = #{categoryName}")
    ItemCategory selectByCategoryName(@Param("categoryName") String categoryName);

    /**
     * 查询所有分类并按排序字段排序
     * 查询所有分类信息，按排序字段升序、创建时间降序排列
     * 主要用于前端分类展示，确保分类按照预设顺序显示
     * 排序规则：
     * 1. 首先按sort_order升序排列，数值越小排序越靠前
     * 2. sort_order相同时，按created_time降序排列，新创建的分类排在前面
     * @return 分类列表，按排序规则排列，如果没有分类则返回空列表
     */
    @Select("SELECT * FROM item_categories ORDER BY sort_order ASC, created_time DESC")
    List<ItemCategory> selectAllOrdered();

    /**
     * 根据分类名称模糊查询
     * 使用LIKE语句进行模糊匹配，查询包含指定关键词的分类信息
     * 支持部分匹配，如"电子"可以匹配"电子产品"、"电子设备"等
     * 通常用于分类搜索和自动补全功能
     * @param keyword 搜索关键词，不需要添加通配符，方法会自动添加
     * @return 包含关键词的分类列表，如果没有匹配项则返回空列表
     */
    @Select("SELECT * FROM item_categories WHERE category_name LIKE CONCAT('%', #{keyword}, '%')")
    List<ItemCategory> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 获取最大的排序值
     * 查询所有分类中最大的sort_order值
     * 通常用于添加新分类时确定下一个可用的排序值
     * 如果没有分类记录，则返回null
     * @return 最大排序值，如果没有分类则返回null
     */
    @Select("SELECT MAX(sort_order) FROM item_categories")
    Integer selectMaxSortOrder();

    /**
     * 更新分类排序
     * 更新指定分类的排序值，用于调整分类显示顺序
     * 通常在管理后台进行分类排序调整时使用
     * @param categoryId 分类ID，必须是已存在的分类ID
     * @param newSortOrder 新的排序值，应为正整数
     * @return 影响的行数，1表示更新成功，0表示分类ID不存在
     */
    @Update("UPDATE item_categories SET sort_order = #{newSortOrder} WHERE id = #{categoryId}")
    int updateSortOrder(@Param("categoryId") Long categoryId, @Param("newSortOrder") Integer newSortOrder);
}