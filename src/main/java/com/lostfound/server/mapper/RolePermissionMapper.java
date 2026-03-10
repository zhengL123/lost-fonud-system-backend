package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 * 
 * 提供角色权限关联数据的数据库操作
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色ID查询角色权限关联列表
     * 
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    List<RolePermission> getRolePermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据权限ID查询角色权限关联列表
     * 
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    List<RolePermission> getRolePermissionsByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 根据角色ID和权限ID查询角色权限关联
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 角色权限关联
     */
    RolePermission getRolePermissionByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 根据角色ID删除角色权限关联
     * 
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRolePermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据权限ID删除角色权限关联
     * 
     * @param permissionId 权限ID
     * @return 影响行数
     */
    int deleteRolePermissionsByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 批量插入角色权限关联
     * 
     * @param rolePermissions 角色权限关联列表
     * @return 影响行数
     */
    int batchInsertRolePermissions(@Param("list") List<RolePermission> rolePermissions);
}