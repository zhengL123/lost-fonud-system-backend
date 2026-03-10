package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 * 
 * 提供角色管理的业务逻辑处理
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 分页查询角色
     * 
     * @param page 分页参数
     * @param name 角色名称（模糊查询）
     * @param status 角色状态
     * @return 角色分页数据
     */
    Page<Role> getRolePage(Page<Role> page, String name, Integer status);
    
    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<com.lostfound.server.entity.Permission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 根据角色编码查询角色
     * 
     * @param code 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(String code);
    
    /**
     * 根据角色名称查询角色
     * 
     * @param name 角色名称
     * @return 角色信息
     */
    Role getRoleByName(String name);
    
    /**
     * 查询所有启用的角色
     * 
     * @return 角色列表
     */
    List<Role> getAllEnabledRoles();
    
    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @param permissionIds 权限ID列表
     * @return 创建结果
     */
    boolean createRole(Role role, List<Long> permissionIds);
    
    /**
     * 更新角色
     * 
     * @param role 角色信息
     * @param permissionIds 权限ID列表
     * @return 更新结果
     */
    boolean updateRole(Role role, List<Long> permissionIds);
    
    /**
     * 删除角色
     * 
     * @param roleId 角色ID
     * @return 删除结果
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 批量删除角色
     * 
     * @param roleIds 角色ID列表
     * @return 删除结果
     */
    boolean batchDeleteRoles(List<Long> roleIds);
    
    /**
     * 启用/禁用角色
     * 
     * @param roleId 角色ID
     * @param status 状态（0：禁用，1：启用）
     * @return 操作结果
     */
    boolean changeRoleStatus(Long roleId, Integer status);
    
    /**
     * 分配权限给角色
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 移除角色的权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds);
}