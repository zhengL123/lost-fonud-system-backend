package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 * 
 * 用于管理系统中的权限信息
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
@TableName("permission")
public class Permission {
    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限编码
     */
    private String code;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 权限类型（0：菜单，1：按钮，2：接口）
     */
    private Integer type;
    
    /**
     * 父权限ID
     */
    private Long parentId;
    
    /**
     * 权限路径（URL路径）
     */
    private String path;
    
    /**
     * 权限图标
     */
    private String icon;
    
    /**
     * 权限状态（0：禁用，1：启用）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 更新者ID
     */
    private Long updaterId;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 子权限列表（非数据库字段）
     */
    private List<Permission> children;
}