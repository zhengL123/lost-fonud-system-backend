package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 * 
 * 用于管理系统中的角色信息
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
@TableName("role")
public class Role {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色编码
     */
    private String code;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 角色状态（0：禁用，1：启用）
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
     * 角色关联的权限列表（非数据库字段）
     */
    private List<Permission> permissions;
}