package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    @Cacheable(value = "users", key = "'username:' + #username", unless = "#result == null")
    User getUserByUsername(String username);

    @Cacheable(value = "users", key = "'email:' + #email", unless = "#result == null")
    User getUserByEmail(String email);

    @Cacheable(value = "users", key = "'phone:' + #phone", unless = "#result == null")
    User getUserByPhone(String phone);
    
    // 注册验证相关方法（不使用缓存，因为需要检查所有状态的用户）
    User getUserByUsernameForRegistration(String username);

    User getUserByEmailForRegistration(String email);

    User getUserByPhoneForRegistration(String phone);

    @Cacheable(value = "users", key = "'role:' + #role", unless = "#result == null || #result.isEmpty()")
    List<User> getUsersByRole(String role);

    @Cacheable(value = "users", key = "'status:' + #status", unless = "#result == null || #result.isEmpty()")
    List<User> getUsersByStatus(String status);

    @CacheEvict(value = "users", allEntries = true)
    boolean updateUserStatus(Long userId, String status);

    Page<User> getUserPage(Integer pageNum, Integer pageSize, String username, String role, String status);

    boolean isUsernameExists(String username, Long excludeUserId);

    boolean isEmailExists(String email, Long excludeUserId);

    boolean isPhoneExists(String phone, Long excludeUserId);

    @Cacheable(value = "users", key = "'stats'")
    Map<String, Object> getUserStats();

    @CacheEvict(value = "users", key = "'email:' + #email")
    boolean resetPassword(String email, String newPassword);

    @CacheEvict(value = "users", allEntries = true)
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}