package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.Permission;
import com.lostfound.server.mapper.PermissionMapper;
import com.lostfound.server.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务实现类
 * 
 * 实现权限管理的业务逻辑处理
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public Page<Permission> getPermissionPage(Page<Permission> page, String name, Integer type, Integer status) {
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Permission::getName, name);
        queryWrapper.eq(type != null, Permission::getType, type);
        queryWrapper.eq(status != null, Permission::getStatus, status);
        queryWrapper.orderByAsc(Permission::getSortOrder);
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.getPermissionsByUserId(userId);
    }
    
    @Override
    public Permission getPermissionByCode(String code) {
        return permissionMapper.getPermissionByCode(code);
    }
    
    @Override
    public Permission getPermissionByName(String name) {
        return permissionMapper.getPermissionByName(name);
    }
    
    @Override
    public List<Permission> getAllEnabledPermissions() {
        return permissionMapper.getAllEnabledPermissions();
    }
    
    @Override
    public List<Permission> getPermissionsByParentId(Long parentId) {
        return permissionMapper.getPermissionsByParentId(parentId);
    }
    
    @Override
    public List<Permission> getPermissionsByType(Integer type) {
        return permissionMapper.getPermissionsByType(type);
    }
    
    @Override
    public List<Permission> getPermissionTree() {
        // 查询所有启用的权限
        List<Permission> allPermissions = permissionMapper.getAllEnabledPermissions();
        
        if (CollectionUtils.isEmpty(allPermissions)) {
            return new ArrayList<>();
        }
        
        // 构建权限树
        return buildPermissionTree(allPermissions, 0L);
    }
    
    /**
     * 构建权限树
     * 
     * @param permissions 所有权限列表
     * @param parentId 父权限ID
     * @return 权限树
     */
    private List<Permission> buildPermissionTree(List<Permission> permissions, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        
        for (Permission permission : permissions) {
            if (permission.getParentId() != null && permission.getParentId().equals(parentId)) {
                // 递归构建子权限
                List<Permission> children = buildPermissionTree(permissions, permission.getId());
                permission.setChildren(children);
                
                tree.add(permission);
            }
        }
        
        return tree;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPermission(Permission permission) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        permission.setCreateTime(now);
        permission.setUpdateTime(now);
        
        // 如果没有指定父权限ID，默认为0（根权限）
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        
        return this.save(permission);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        // 设置更新时间
        permission.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(permission);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getParentId, permissionId);
        long childCount = this.count(queryWrapper);
        
        if (childCount > 0) {
            throw new RuntimeException("存在子权限，无法删除");
        }
        
        return this.removeById(permissionId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeletePermissions(List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return false;
        }
        
        // 检查是否有子权限
        for (Long permissionId : permissionIds) {
            LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Permission::getParentId, permissionId);
            long childCount = this.count(queryWrapper);
            
            if (childCount > 0) {
                throw new RuntimeException("权限ID " + permissionId + " 存在子权限，无法删除");
            }
        }
        
        return this.removeByIds(permissionIds);
    }
    
    @Override
    public boolean changePermissionStatus(Long permissionId, Integer status) {
        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setStatus(status);
        permission.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(permission);
    }
    
    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.getPermissionsByRoleId(roleId);
    }
}