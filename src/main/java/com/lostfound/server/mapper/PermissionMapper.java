package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口
 * 
 * 提供权限数据的数据库操作
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    /**
     * 根据用户ID查询权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据权限编码查询权限
     * 
     * @param code 权限编码
     * @return 权限信息
     */
    Permission getPermissionByCode(@Param("code") String code);
    
    /**
     * 根据权限名称查询权限
     * 
     * @param name 权限名称
     * @return 权限信息
     */
    Permission getPermissionByName(@Param("name") String name);
    
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
    List<Permission> getPermissionsByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据权限类型查询权限列表
     * 
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> getPermissionsByType(@Param("type") Integer type);
    
    /**
     * 查询权限树结构
     * 
     * @return 权限树列表
     */
    List<Permission> getPermissionTree();
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(@Param("roleId") Long roleId);
}