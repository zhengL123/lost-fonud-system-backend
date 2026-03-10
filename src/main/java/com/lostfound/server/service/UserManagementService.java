package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.User;

import java.util.List;

/**
 * 用户管理服务接口
 * 定义用户管理的通用方法，避免在多个服务中重复实现
 */
public interface UserManagementService {
    
    /**
     * 获取用户分页数据
     */
    Page<User> getUserPage(Integer pageNum, Integer pageSize, String username, String role, String status);
    
    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户
     */
    User getUserByEmail(String email);
    
    /**
     * 根据手机号获取用户
     */
    User getUserByPhone(String phone);
    
    /**
     * 保存用户
     */
    boolean saveUser(User user);
    
    /**
     * 更新用户
     */
    boolean updateUser(User user);
    
    /**
     * 根据ID删除用户
     */
    boolean deleteUserById(Long id);
    
    /**
     * 批量删除用户
     */
    boolean deleteUsersByIds(List<Long> ids);
    
    /**
     * 获取所有用户数量
     */
    long countUsers();
    
    /**
     * 根据条件获取用户数量
     */
    long countUsers(String role, String status);
}