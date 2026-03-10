package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.util.Result;
import com.lostfound.server.entity.Permission;
import com.lostfound.server.service.PermissionService;
import com.lostfound.server.util.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 * 
 * 提供权限管理的API接口
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 分页查询权限
     */
    @Operation(summary = "分页查询权限", description = "根据权限名称、类型和状态分页查询权限列表")
    @GetMapping
    public Result<PageResult<Permission>> getPermissionPage(
            @Parameter(description = "当前页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "权限名称", example = "用户管理") 
            @RequestParam(required = false) String name,
            @Parameter(description = "权限类型", example = "0") 
            @RequestParam(required = false) Integer type,
            @Parameter(description = "权限状态", example = "1") 
            @RequestParam(required = false) Integer status) {
        
        Page<Permission> page = new Page<>(current, size);
        Page<Permission> permissionPage = permissionService.getPermissionPage(page, name, type, status);
        // 转换为自定义分页响应格式
        PageResult<Permission> pageResult = PageResult.of(permissionPage);
        return Result.success(pageResult);
    }
    
    /**
     * 根据用户ID查询权限列表
     */
    @Operation(summary = "根据用户ID查询权限列表", description = "获取指定用户的权限列表")
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(
            @Parameter(description = "用户ID", required = true) 
            @PathVariable Long userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }
    
    /**
     * 根据权限编码查询权限
     */
    @Operation(summary = "根据权限编码查询权限", description = "根据权限编码获取权限信息")
    @GetMapping("/code/{code}")
    public Result<Permission> getPermissionByCode(
            @Parameter(description = "权限编码", required = true) 
            @PathVariable String code) {
        Permission permission = permissionService.getPermissionByCode(code);
        return Result.success(permission);
    }
    
    /**
     * 根据权限名称查询权限
     */
    @Operation(summary = "根据权限名称查询权限", description = "根据权限名称获取权限信息")
    @GetMapping("/name/{name}")
    public Result<Permission> getPermissionByName(
            @Parameter(description = "权限名称", required = true) 
            @PathVariable String name) {
        Permission permission = permissionService.getPermissionByName(name);
        return Result.success(permission);
    }
    
    /**
     * 查询所有启用的权限
     */
    @Operation(summary = "查询所有启用的权限", description = "获取所有状态为启用的权限列表")
    @GetMapping("/enabled")
    public Result<List<Permission>> getAllEnabledPermissions() {
        List<Permission> permissions = permissionService.getAllEnabledPermissions();
        return Result.success(permissions);
    }
    
    /**
     * 根据父权限ID查询子权限列表
     */
    @Operation(summary = "根据父权限ID查询子权限列表", description = "获取指定父权限下的所有子权限")
    @GetMapping("/parent/{parentId}")
    public Result<List<Permission>> getPermissionsByParentId(
            @Parameter(description = "父权限ID", required = true) 
            @PathVariable Long parentId) {
        List<Permission> permissions = permissionService.getPermissionsByParentId(parentId);
        return Result.success(permissions);
    }
    
    /**
     * 根据权限类型查询权限列表
     */
    @Operation(summary = "根据权限类型查询权限列表", description = "根据权限类型获取权限列表")
    @GetMapping("/type/{type}")
    public Result<List<Permission>> getPermissionsByType(
            @Parameter(description = "权限类型", required = true) 
            @PathVariable Integer type) {
        List<Permission> permissions = permissionService.getPermissionsByType(type);
        return Result.success(permissions);
    }
    
    /**
     * 查询权限树结构
     */
    @Operation(summary = "查询权限树结构", description = "获取权限的树形结构数据")
    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> permissionTree = permissionService.getPermissionTree();
        return Result.success(permissionTree);
    }
    
    /**
     * 根据ID查询权限
     */
    @Operation(summary = "根据ID查询权限", description = "根据权限ID获取权限详细信息")
    @GetMapping("/{id}")
    public Result<Permission> getPermissionById(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }
    
    /**
     * 创建权限
     */
    @Operation(summary = "创建权限", description = "创建新的权限")
    @PostMapping
    public Result<Boolean> createPermission(
            @Parameter(description = "权限信息", required = true) 
            @RequestBody Permission permission) {
        boolean result = permissionService.createPermission(permission);
        return Result.success(result);
    }
    
    /**
     * 更新权限
     */
    @Operation(summary = "更新权限", description = "更新权限信息")
    @PutMapping("/{id}")
    public Result<Boolean> updatePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "权限信息", required = true) 
            @RequestBody Permission permission) {
        permission.setId(id);
        boolean result = permissionService.updatePermission(permission);
        return Result.success(result);
    }
    
    /**
     * 删除权限
     */
    @Operation(summary = "删除权限", description = "根据ID删除权限")
    @DeleteMapping("/{id}")
    public Result<Boolean> deletePermission(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable Long id) {
        boolean result = permissionService.deletePermission(id);
        return Result.success(result);
    }
    
    /**
     * 批量删除权限
     */
    @Operation(summary = "批量删除权限", description = "根据ID列表批量删除权限")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeletePermissions(
            @Parameter(description = "权限ID列表", required = true) 
            @RequestBody List<Long> permissionIds) {
        boolean result = permissionService.batchDeletePermissions(permissionIds);
        return Result.success(result);
    }
    
    /**
     * 启用/禁用权限
     */
    @Operation(summary = "启用/禁用权限", description = "切换权限的启用/禁用状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> changePermissionStatus(
            @Parameter(description = "权限ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "状态（0：禁用，1：启用）", required = true) 
            @RequestParam Integer status) {
        boolean result = permissionService.changePermissionStatus(id, status);
        return Result.success(result);
    }
    
    /**
     * 根据角色ID查询权限列表
     */
    @Operation(summary = "根据角色ID查询权限列表", description = "获取指定角色的权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
}