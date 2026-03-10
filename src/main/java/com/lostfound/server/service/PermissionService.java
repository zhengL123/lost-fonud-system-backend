package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 * 
 * 提供权限管理的业务逻辑处理
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 分页查询权限
     * 
     * @param page 分页参数
     * @param name 权限名称（模糊查询）
     * @param type 权限类型
     * @param status 权限状态
     * @return 权限分页数据
     */
    Page<Permission> getPermissionPage(Page<Permission> page, String name, Integer type, Integer status);
    
    /**
     * 根据用户ID查询权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);
    
    /**
     * 根据权限编码查询权限
     * 
     * @param code 权限编码
     * @return 权限信息
     */
    Permission getPermissionByCode(String code);
    
    /**
     * 根据权限名称查询权限
     * 
     * @param name 权限名称
     * @return 权限信息
     */
    Permission getPermissionByName(String name);
    
    /**
     * 查询所有启用的权限
     * 
     * @return 权限列表
     */
    List<Permission> getAllEnabledPermissions();
    
    /**
     * 根据父权限ID查询子权限列表
     * 
     * @param parentId 父权限ID
     * @return 子权限列表
     */
    List<Permission> getPermissionsByParentId(Long parentId);
    
    /**
     * 根据权限类型查询权限列表
     * 
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> getPermissionsByType(Integer type);
    
    /**
     * 查询权限树结构
     * 
     * @return 权限树列表
     */
    List<Permission> getPermissionTree();
    
    /**
     * 创建权限
     * 
     * @param permission 权限信息
     * @return 创建结果
     */
    boolean createPermission(Permission permission);
    
    /**
     * 更新权限
     * 
     * @param permission 权限信息
     * @return 更新结果
     */
    boolean updatePermission(Permission permission);
    
    /**
     * 删除权限
     * 
     * @param permissionId 权限ID
     * @return 删除结果
     */
    boolean deletePermission(Long permissionId);
    
    /**
     * 批量删除权限
     * 
     * @param permissionIds 权限ID列表
     * @return 删除结果
     */
    boolean batchDeletePermissions(List<Long> permissionIds);
    
    /**
     * 启用/禁用权限
     * 
     * @param permissionId 权限ID
     * @param status 状态（0：禁用，1：启用）
     * @return 操作结果
     */
    boolean changePermissionStatus(Long permissionId, Integer status);
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
}