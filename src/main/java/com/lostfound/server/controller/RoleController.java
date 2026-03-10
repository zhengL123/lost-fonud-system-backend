package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.util.Result;
import com.lostfound.server.entity.Permission;
import com.lostfound.server.entity.Role;
import com.lostfound.server.service.RoleService;
import com.lostfound.server.util.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 
 * 提供角色管理的API接口
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色", description = "根据角色名称和状态分页查询角色列表")
    @GetMapping
    public Result<PageResult<Role>> getRolePage(
            @Parameter(description = "当前页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "角色名称", example = "管理员") 
            @RequestParam(required = false) String name,
            @Parameter(description = "角色状态", example = "1") 
            @RequestParam(required = false) Integer status) {
        
        Page<Role> page = new Page<>(current, size);
        Page<Role> rolePage = roleService.getRolePage(page, name, status);
        // 转换为自定义分页响应格式
        PageResult<Role> pageResult = PageResult.of(rolePage);
        return Result.success(pageResult);
    }
    
    /**
     * 根据用户ID查询角色列表
     */
    @Operation(summary = "根据用户ID查询角色列表", description = "获取指定用户的角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(
            @Parameter(description = "用户ID", required = true) 
            @PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        return Result.success(roles);
    }
    
    /**
     * 根据角色ID查询权限列表
     */
    @Operation(summary = "根据角色ID查询权限列表", description = "获取指定角色的权限列表")
    @GetMapping("/{roleId}/permissions")
    public Result<List<Permission>> getPermissionsByRoleId(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long roleId) {
        List<Permission> permissions = roleService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
    
    /**
     * 根据角色编码查询角色
     */
    @Operation(summary = "根据角色编码查询角色", description = "根据角色编码获取角色信息")
    @GetMapping("/code/{code}")
    public Result<Role> getRoleByCode(
            @Parameter(description = "角色编码", required = true) 
            @PathVariable String code) {
        Role role = roleService.getRoleByCode(code);
        return Result.success(role);
    }
    
    /**
     * 根据角色名称查询角色
     */
    @Operation(summary = "根据角色名称查询角色", description = "根据角色名称获取角色信息")
    @GetMapping("/name/{name}")
    public Result<Role> getRoleByName(
            @Parameter(description = "角色名称", required = true) 
            @PathVariable String name) {
        Role role = roleService.getRoleByName(name);
        return Result.success(role);
    }
    
    /**
     * 查询所有启用的角色
     */
    @Operation(summary = "查询所有启用的角色", description = "获取所有状态为启用的角色列表")
    @GetMapping("/enabled")
    public Result<List<Role>> getAllEnabledRoles() {
        List<Role> roles = roleService.getAllEnabledRoles();
        return Result.success(roles);
    }
    
    /**
     * 根据ID查询角色
     */
    @Operation(summary = "根据ID查询角色", description = "根据角色ID获取角色详细信息")
    @GetMapping("/{id}")
    public Result<Role> getRoleById(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role != null) {
            // 查询角色权限
            List<Permission> permissions = roleService.getPermissionsByRoleId(id);
            role.setPermissions(permissions);
        }
        return Result.success(role);
    }
    
    /**
     * 创建角色
     */
    @Operation(summary = "创建角色", description = "创建新的角色并分配权限")
    @PostMapping
    public Result<Boolean> createRole(
            @Parameter(description = "角色信息", required = true) 
            @RequestBody Role role,
            @Parameter(description = "权限ID列表") 
            @RequestParam(required = false) List<Long> permissionIds) {
        boolean result = roleService.createRole(role, permissionIds);
        return Result.success(result);
    }
    
    /**
     * 更新角色
     */
    @Operation(summary = "更新角色", description = "更新角色信息并重新分配权限")
    @PutMapping("/{id}")
    public Result<Boolean> updateRole(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "角色信息", required = true) 
            @RequestBody Role role,
            @Parameter(description = "权限ID列表") 
            @RequestParam(required = false) List<Long> permissionIds) {
        role.setId(id);
        boolean result = roleService.updateRole(role, permissionIds);
        return Result.success(result);
    }
    
    /**
     * 删除角色
     */
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteRole(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long id) {
        boolean result = roleService.deleteRole(id);
        return Result.success(result);
    }
    
    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色", description = "根据ID列表批量删除角色")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteRoles(
            @Parameter(description = "角色ID列表", required = true) 
            @RequestBody List<Long> roleIds) {
        boolean result = roleService.batchDeleteRoles(roleIds);
        return Result.success(result);
    }
    
    /**
     * 启用/禁用角色
     */
    @Operation(summary = "启用/禁用角色", description = "切换角色的启用/禁用状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> changeRoleStatus(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "状态（0：禁用，1：启用）", required = true) 
            @RequestParam Integer status) {
        boolean result = roleService.changeRoleStatus(id, status);
        return Result.success(result);
    }
    
    /**
     * 分配权限给角色
     */
    @Operation(summary = "分配权限给角色", description = "为角色分配指定的权限列表")
    @PostMapping("/{roleId}/permissions")
    public Result<Boolean> assignPermissionsToRole(
            @Parameter(description = "角色ID", required = true) 
            @PathVariable Long roleId,
            @Parameter(description = "权限ID列表", required = true) 
            @RequestBody List<Long> permissionIds) {
        boolean result = roleService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success(result);
    }
}