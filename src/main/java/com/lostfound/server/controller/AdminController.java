package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.LostItem;
import com.lostfound.server.entity.User;
import com.lostfound.server.service.ItemCategoryService;
import com.lostfound.server.service.impl.AdminServiceImpl;
import com.lostfound.server.service.UserService;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员专用的功能接口，包括：
 * 1. 系统统计数据获取（用户数量、物品数量、各类别统计等）
 * 2. 用户管理功能（用户列表、添加、更新、删除、状态切换、密码重置、批量操作）
 * 3. 失物物品管理功能（物品列表、添加、更新、删除、状态切换）
 * 
 * 注意：所有接口都需要管理员权限验证，通过Spring Security或JWT Token验证
 * 
 * @author 系统开发团队
 * @version 1.0
 * @since 2023-01-01
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // 注入管理员服务层，处理管理员特有的业务逻辑
    private final AdminServiceImpl adminServiceImpl;
    // 注入用户服务层，处理用户相关操作
    private final UserService userService;
    // 注入失物服务层，处理失物物品相关操作
    private final LostItemService lostItemService;
    // 注入物品分类服务层，处理物品分类相关操作
    private final ItemCategoryService itemCategoryService;

    /**
     * 获取管理员统计数据
     * 
     * 提供系统整体统计数据，包括：
     * - 用户总数、活跃用户数、管理员数量
     * - 失物物品总数、各状态物品数量（丢失中、已找到、已归还）
     * - 各类别物品数量统计
     * - 最近一段时间内的物品发布和认领趋势
     * 
     * @return Result<Map<String, Object>> 包含各类统计数据的Map对象
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return adminServiceImpl.getStatistics();
    }

    /**
     * 获取最近活动
     * 
     * 提供系统最近的活动记录，包括：
     * - 最新发布的失物信息
     * - 最新注册的用户
     * - 最新的状态变更记录
     * 
     * @return Result<Map<String, Object>> 包含最近活动数据的Map对象
     */
    @GetMapping("/recent-activities")
    public Result<Map<String, Object>> getRecentActivities() {
        return adminServiceImpl.getRecentActivities();
    }

    /**
     * 获取用户列表（分页）
     * 
     * 管理员专用的用户列表查询接口，支持多条件筛选和分页
     * 相比普通用户接口，此接口可以查看所有用户的详细信息
     * 
     * @param pageNum 页码，从1开始，默认为1
     * @param pageSize 每页记录数，默认为10，最大值为100
     * @param username 用户名，支持模糊查询（可选）
     * @param role 用户角色，精确匹配（USER/ADMIN）（可选）
     * @param status 用户状态，精确匹配（ACTIVE/INACTIVE）（可选）
     * @return Result<Page<User>> 包含分页数据和分页信息的Page对象
     */
    @GetMapping("/users/page")
    public Result<Page<User>> getUsersPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return Result.success(userService.getUserPage(pageNum, pageSize, username, role, status));
    }

    /**
     * 获取用户列表（分页）
     */
    // @GetMapping("/users")
    // public Result<Page<User>> getUsers(
    //         @RequestParam(defaultValue = "1") Integer pageNum,
    //         @RequestParam(defaultValue = "10") Integer pageSize,
    //         @RequestParam(required = false) String username,
    //         @RequestParam(required = false) String role,
    //         @RequestParam(required = false) String status) {
    //     return Result.success(userService.getUserPage(pageNum, pageSize, username, role, status));
    // }

    /**
     * 管理员添加用户
     */
    @PostMapping("/users")
    public Result<String> addUser(@RequestBody User user) {
        return adminServiceImpl.addUser(user);
    }

    /**
     * 管理员更新用户信息
     */
    @PutMapping("/users/{id}")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        return adminServiceImpl.updateUser(id, user);
    }

    /**
     * 管理员删除用户
     */
    @DeleteMapping("/users/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        return adminServiceImpl.deleteUser(id);
    }

    /**
     * 切换用户状态（启用/禁用）
     */
    @PutMapping("/users/{id}/status")
    public Result<String> toggleUserStatus(@PathVariable Long id, @RequestParam String status) {
        return adminServiceImpl.toggleUserStatus(id, status);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetPassword(@PathVariable Long id) {
        return adminServiceImpl.resetPassword(id, null);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/users/batch")
    public Result<String> batchDeleteUsers(@RequestParam Long[] ids) {
        return adminServiceImpl.batchDeleteUsers(ids);
    }
    
    // ==================== 物品管理相关方法 ====================
    
    /**
     * 获取物品列表（分页）
     * 
     * 管理员专用的物品列表查询接口，支持多条件筛选和分页
     * 相比普通用户接口，此接口可以查看所有状态的物品，包括已删除的物品
     * 
     * @param pageNum 页码，从1开始，默认为1
     * @param pageSize 每页记录数，默认为10，最大值为100
     * @param itemName 物品名称，支持模糊查询（可选）
     * @param categoryId 物品分类ID（可选）
     * @param status 物品状态，精确匹配（LOST/FOUND/RETURNED）（可选）
     * @return Result<Page<LostItem>> 包含分页数据和分页信息的Page对象
     */
    @GetMapping("/items")
    public Result<Page<LostItem>> getItems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        Page<LostItem> page = new Page<>(pageNum, pageSize);
        
        // 使用自定义方法获取包含类别名称的分页数据
        Page<LostItem> result = lostItemService.pageWithCategoryName(page, itemName, categoryId, status, null);
        
        return Result.success(result);
    }
    
    /**
     * 管理员添加物品
     * 
     * 管理员专用的物品添加接口，可以添加任何状态的物品
     * 相比普通用户接口，此接口不需要验证发布者身份
     * 
     * @param item 物品信息对象，包含物品名称、描述、丢失地点、状态等信息
     * @return Result<LostItem> 包含操作结果和添加后的物品信息
     */
    @PostMapping("/items")
    public Result<LostItem> addItem(@RequestBody LostItem item) {
        boolean saved = lostItemService.save(item);
        if (saved) {
            return Result.success("添加成功", item);
        } else {
            return Result.error("添加失败");
        }
    }
    
    /**
     * 管理员更新物品信息
     * 
     * 管理员专用的物品更新接口，可以修改任何物品的任何信息
     * 相比普通用户接口，此接口可以修改物品状态和其他所有字段
     * 
     * @param id 要更新的物品ID
     * @param item 包含更新信息的物品对象
     * @return Result<LostItem> 包含操作结果和更新后的物品信息
     */
    @PutMapping("/items/{id}")
    public Result<LostItem> updateItem(@PathVariable Long id, @RequestBody LostItem item) {
        item.setId(id);
        boolean updated = lostItemService.updateById(item);
        if (updated) {
            return Result.success("更新成功", item);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 管理员删除物品
     * 
     * 管理员专用的物品删除接口，可以删除任何物品
     * 注意：此操作为物理删除，不可恢复，建议使用状态更新代替删除
     * 
     * @param id 要删除的物品ID
     * @return Result<String> 包含操作结果信息
     */
    @DeleteMapping("/items/{id}")
    public Result<String> deleteItem(@PathVariable Long id) {
        boolean removed = lostItemService.removeById(id);
        if (removed) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 切换物品状态
     * 
     * 管理员专用的物品状态更新接口，可以修改任何物品的状态
     * 支持的状态转换：
     * - LOST（丢失中）→ FOUND（已找到）
     * - FOUND（已找到）→ RETURNED（已归还）
     * - 任何状态 → LOST（重新标记为丢失中）
     * 
     * @param id 要更新状态的物品ID
     * @param status 新的状态值（LOST/FOUND/RETURNED）
     * @return Result<String> 包含操作结果信息
     */
    @PutMapping("/items/{id}/status")
    public Result<String> updateItemStatus(@PathVariable Long id, @RequestParam String status) {
        LostItem item = lostItemService.getById(id);
        if (item == null) {
            return Result.error("物品不存在");
        }
        
        item.setStatus(status);
        boolean updated = lostItemService.updateById(item);
        if (updated) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
}