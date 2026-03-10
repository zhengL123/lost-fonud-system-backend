package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 * 
 * 提供角色数据的数据库操作
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据角色ID查询权限详情列表
     * 
     * @param roleId 角色ID
     * @return 权限详情列表
     */
    List<com.lostfound.server.entity.Permission> getPermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据角色编码查询角色
     * 
     * @param code 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(@Param("code") String code);
    
    /**
     * 根据角色名称查询角色
     * 
     * @param name 角色名称
     * @return 角色信息
     */
    Role getRoleByName(@Param("name") String name);
    
    /**
     * 查询所有启用的角色
     * 
     * @return 角色列表
     */
    List<Role> getAllEnabledRoles();
    
    /**
     * 批量插入角色权限关联
     * 
     * @param rolePermissions 角色权限关联列表
     * @return 影响行数
     */
    int batchInsertRolePermissions(@Param("list") List<com.lostfound.server.entity.RolePermission> rolePermissions);
    
    /**
     * 根据角色ID删除角色权限关联
     * 
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRolePermissionsByRoleId(@Param("roleId") Long roleId);
}