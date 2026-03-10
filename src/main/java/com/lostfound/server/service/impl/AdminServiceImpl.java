package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lostfound.server.entity.ClaimRecord;
import com.lostfound.server.entity.LostItem;
import com.lostfound.server.entity.User;
import com.lostfound.server.service.ClaimRecordService;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.service.UserService;
import com.lostfound.server.util.AdminUtils;
import com.lostfound.server.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务类
 * 专注于管理员特有的业务逻辑，提供系统管理功能
 * 
 * 主要功能包括：
 * 1. 系统统计数据获取 - 提供用户、失物、认领记录等多维度统计信息
 * 2. 用户管理 - 提供用户的增删改查、状态切换、密码重置等管理功能
 * 3. 数据验证 - 提供用户信息的完整性验证，确保数据一致性
 * 
 * 与普通用户服务的区别：
 * - 拥有更高的权限，可以操作所有用户数据
 * - 提供批量操作功能，提高管理效率
 * - 包含额外的验证逻辑，确保系统安全
 */
@Service
public class AdminServiceImpl {
    
    /**
     * 注入用户服务，用于处理用户相关的业务逻辑
     */
    @Autowired
    private UserService userService;
    
    /**
     * 注入失物服务，用于处理失物相关的业务逻辑
     */
    @Autowired
    private LostItemService lostItemService;
    
    /**
     * 注入认领记录服务，用于处理认领记录相关的业务逻辑
     */
    @Autowired
    private ClaimRecordService claimRecordService;
    
    /**
     * 获取系统统计数据
     * 
     * 统计数据包括：
     * 1. 用户统计 - 总用户数、活跃用户数、管理员数、普通用户数
     * 2. 失物统计 - 总失物数、已找到数、待寻找数、已归还数
     * 3. 认领统计 - 总认领数、待审核数、已通过数、已拒绝数
     * 
     * @return 包含各类统计数据的Map，包装在Result对象中
     */
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户统计
        statistics.put("totalUsers", userService.count());
        statistics.put("activeUsers", userService.count(new QueryWrapper<User>().eq("status", "ACTIVE")));
        statistics.put("adminUsers", userService.count(new QueryWrapper<User>().eq("role", "ADMIN")));
        statistics.put("normalUsers", userService.count(new QueryWrapper<User>().eq("role", "USER")));
        
        // 失物统计
        statistics.put("totalItems", lostItemService.count());
        statistics.put("foundItems", lostItemService.count(new QueryWrapper<LostItem>().eq("status", "FOUND")));
        statistics.put("pendingItems", lostItemService.count(new QueryWrapper<LostItem>().eq("status", "LOST")));
        statistics.put("returnedItems", lostItemService.count(new QueryWrapper<LostItem>().eq("status", "RETURNED")));
        
        // 认领统计
        statistics.put("totalClaims", claimRecordService.count());
        statistics.put("pendingClaims", claimRecordService.count(new QueryWrapper<ClaimRecord>().eq("status", "PENDING")));
        statistics.put("approvedClaims", claimRecordService.count(new QueryWrapper<ClaimRecord>().eq("status", "APPROVED")));
        statistics.put("rejectedClaims", claimRecordService.count(new QueryWrapper<ClaimRecord>().eq("status", "REJECTED")));
        
        return Result.success(statistics);
    }
    
    /**
     * 获取最近活动
     * 
     * 提供系统最近的活动记录，包括：
     * 1. 最新发布的失物信息（最近5条）
     * 2. 最新注册的用户（最近5条）
     * 3. 最新的状态变更记录（最近5条）
     * 
     * @return 包含最近活动数据的Map，包装在Result对象中
     */
    public Result<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        // 最新发布的失物信息（最近5条）
        List<LostItem> recentItems = lostItemService.list(
            new QueryWrapper<LostItem>()
                .orderByDesc("created_time")
                .last("LIMIT 5")
        );
        activities.put("recentItems", recentItems);
        
        // 最新注册的用户（最近5条）
        List<User> recentUsers = userService.list(
            new QueryWrapper<User>()
                .orderByDesc("created_time")
                .last("LIMIT 5")
        );
        activities.put("recentUsers", recentUsers);
        
        // 最新的认领记录（最近5条）
        List<ClaimRecord> recentClaims = claimRecordService.list(
            new QueryWrapper<ClaimRecord>()
                .orderByDesc("created_time")
                .last("LIMIT 5")
        );
        activities.put("recentClaims", recentClaims);
        
        return Result.success(activities);
    }
    
    /**
     * 管理员添加用户
     * 
     * 功能说明：
     * - 验证用户信息的完整性和唯一性
     * - 自动设置默认值（密码、角色、状态）
     * - 对密码进行加密处理
     * 
     * 默认值设置：
     * - 密码：如果未提供，使用系统默认密码
     * - 角色：如果未指定，默认为"USER"
     * - 状态：如果未指定，默认为"ACTIVE"
     * 
     * @param user 要添加的用户对象，包含用户基本信息
     * @return Result对象，成功时返回"添加用户成功"，失败时返回具体错误信息
     */
    public Result<String> addUser(User user) {
        // 验证用户信息
        Result<String> validationResult = validateUser(user, false);
        if (validationResult != null) {
            return validationResult;
        }
        
        // 设置默认值
        user.setPassword(AdminUtils.encodePassword(
            AdminUtils.isEmpty(user.getPassword()) ? AdminUtils.getDefaultPassword() : user.getPassword()
        ));
        user.setRole(AdminUtils.isEmpty(user.getRole()) ? "USER" : user.getRole());
        user.setStatus(AdminUtils.isEmpty(user.getStatus()) ? "ACTIVE" : user.getStatus());
        
        boolean success = userService.save(user);
        return success ? Result.success("添加用户成功") : Result.error("添加用户失败");
    }
    
    /**
     * 管理员更新用户信息
     * 
     * 功能说明：
     * - 验证用户是否存在
     * - 验证更新信息的完整性和唯一性
     * - 对新密码进行加密处理（如果提供）
     * - 保留未修改的原始信息
     * 
     * 注意事项：
     * - 用户ID必须存在于系统中
     * - 用户名、邮箱、手机号不能与已有用户重复
     * - 如果未提供新密码，则保留原密码
     * 
     * @param id 要更新的用户ID
     * @param user 包含更新信息的用户对象
     * @return Result对象，成功时返回"更新用户成功"，失败时返回具体错误信息
     */
    public Result<String> updateUser(Long id, User user) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 设置ID用于验证
        user.setId(id);
        
        // 验证用户信息
        Result<String> validationResult = validateUser(user, true);
        if (validationResult != null) {
            return validationResult;
        }
        
        // 如果提供了新密码，则加密
        if (AdminUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(AdminUtils.encodePassword(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        
        boolean success = userService.updateById(user);
        return success ? Result.success("更新用户成功") : Result.error("更新用户失败");
    }
    
    /**
     * 管理员删除用户
     * 
     * 功能说明：
     * - 验证用户是否存在
     * - 从系统中永久删除用户记录
     * 
     * 注意事项：
     * - 此操作不可逆，请谨慎使用
     * - 删除用户后，相关的失物记录和认领记录可能需要额外处理
     * 
     * @param id 要删除的用户ID
     * @return Result对象，成功时返回"删除用户成功"，失败时返回具体错误信息
     */
    public Result<String> deleteUser(Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        boolean success = userService.removeById(id);
        return success ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }
    
    /**
     * 切换用户状态（启用/禁用）
     * 
     * 功能说明：
     * - 根据前端传递的状态值设置用户状态
     * - 禁用状态的用户无法登录系统
     * 
     * 状态转换规则：
     * - ACTIVE -> DISABLED
     * - DISABLED -> ACTIVE
     * 
     * @param id 要切换状态的用户ID
     * @param status 前端传递的状态值
     * @return Result对象，成功时返回"用户状态更新成功"，失败时返回具体错误信息
     */
    public Result<String> toggleUserStatus(Long id, String status) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 使用前端传递的状态值，如果为空则自动切换
        String newStatus;
        if (AdminUtils.isEmpty(status)) {
            newStatus = "ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE";
        } else {
            newStatus = status;
        }
        
        user.setStatus(newStatus);
        boolean success = userService.updateById(user);
        return success ? Result.success("用户状态更新成功") : Result.error("用户状态更新失败");
    }
    
    /**
     * 重置用户密码
     * 
     * 功能说明：
     * - 验证用户是否存在
     * - 重置用户密码为指定的新密码或系统默认密码
     * - 对新密码进行加密处理
     * 
     * 密码规则：
     * - 如果未提供新密码，使用系统默认密码
     * - 新密码会被加密后存储
     * 
     * @param id 要重置密码的用户ID
     * @param newPassword 新密码，可以为null或空字符串（使用默认密码）
     * @return Result对象，成功时返回"重置密码成功，新密码为：[密码]"，失败时返回具体错误信息
     */
    public Result<String> resetPassword(Long id, String newPassword) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 如果没有提供新密码，使用默认密码
        if (AdminUtils.isEmpty(newPassword)) {
            newPassword = AdminUtils.getDefaultPassword();
        }
        
        user.setPassword(AdminUtils.encodePassword(newPassword));
        boolean success = userService.updateById(user);
        return success ? Result.success("重置密码成功，新密码为：" + newPassword) : Result.error("重置密码失败");
    }
    
    /**
     * 批量删除用户
     * 
     * 功能说明：
     * - 一次性删除多个用户
     * - 提高管理员操作效率
     * 
     * 注意事项：
     * - 此操作不可逆，请谨慎使用
     * - 删除用户后，相关的失物记录和认领记录可能需要额外处理
     * - 空数组或null数组将被视为无效操作
     * 
     * @param ids 要删除的用户ID数组
     * @return Result对象，成功时返回"批量删除用户成功"，失败时返回具体错误信息
     */
    public Result<String> batchDeleteUsers(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Result.error("请选择要删除的用户");
        }
        
        boolean success = userService.removeByIds(List.of(ids));
        return success ? Result.success("批量删除用户成功") : Result.error("批量删除用户失败");
    }
    
    /**
     * 验证用户信息
     * 
     * 功能说明：
     * - 验证用户信息的完整性和唯一性
     * - 确保用户名、邮箱、手机号不重复
     * 
     * 验证规则：
     * - 用户名不能为空
     * - 用户名不能与已有用户重复（更新时排除自己）
     * - 邮箱不能与已有用户重复（更新时排除自己）
     * - 手机号不能与已有用户重复（更新时排除自己）
     * 
     * @param user 要验证的用户对象
     * @param isUpdate 是否为更新操作（true：更新，false：新增）
     * @return 验证失败时返回错误信息Result对象，验证成功时返回null
     */
    private Result<String> validateUser(User user, boolean isUpdate) {
        if (AdminUtils.isEmpty(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        
        // 检查用户名是否已存在
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null && (!isUpdate || !existingUser.getId().equals(user.getId()))) {
            return Result.error("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (AdminUtils.isNotEmpty(user.getEmail())) {
            User existingEmailUser = userService.getUserByEmail(user.getEmail());
            if (existingEmailUser != null && (!isUpdate || !existingEmailUser.getId().equals(user.getId()))) {
                return Result.error("邮箱已存在");
            }
        }
        
        // 检查手机号是否已存在
        if (AdminUtils.isNotEmpty(user.getPhone())) {
            User existingPhoneUser = userService.getUserByPhone(user.getPhone());
            if (existingPhoneUser != null && (!isUpdate || !existingPhoneUser.getId().equals(user.getId()))) {
                return Result.error("手机号已存在");
            }
        }
        
        return null;
    }
}