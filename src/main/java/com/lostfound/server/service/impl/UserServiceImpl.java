package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.User;
import com.lostfound.server.mapper.UserMapper;
import com.lostfound.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("status", "ACTIVE"); // 只查询活跃用户
        return getOne(queryWrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("status", "ACTIVE"); // 只查询活跃用户
        return getOne(queryWrapper);
    }

    @Override
    public User getUserByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        queryWrapper.eq("status", "ACTIVE"); // 只查询活跃用户
        return getOne(queryWrapper);
    }
    
    @Override
    public User getUserByUsernameForRegistration(String username) {
        return userMapper.selectByUsernameForRegistration(username);
    }

    @Override
    public User getUserByEmailForRegistration(String email) {
        return userMapper.selectByEmailForRegistration(email);
    }

    @Override
    public User getUserByPhoneForRegistration(String phone) {
        return userMapper.selectByPhoneForRegistration(phone);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        return list(queryWrapper);
    }

    @Override
    public List<User> getUsersByStatus(String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        return list(queryWrapper);
    }

    @Override
    public boolean updateUserStatus(Long userId, String status) {
        // 参数验证
        if (userId == null || !StringUtils.hasText(status)) {
            log.error("更新用户状态参数不完整");
            return false;
        }
        
        User user = getById(userId);
        if (user == null) {
            log.error("用户不存在，ID: {}", userId);
            return false;
        }
        
        user.setStatus(status.trim());
        boolean result = updateById(user);
        
        if (result) {
            log.info("用户状态更新成功，ID: {}, 新状态: {}", userId, status);
        } else {
            log.error("用户状态更新失败，ID: {}", userId);
        }
        
        return result;
    }

    @Override
    public Page<User> getUserPage(Integer pageNum, Integer pageSize, String username, String role, String status) {
        // 参数验证
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }
        
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like("username", username.trim());
        }
        if (StringUtils.hasText(role)) {
            queryWrapper.eq("role", role.trim());
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status.trim());
        }
        
        queryWrapper.orderByDesc("created_time");
        return page(page, queryWrapper);
    }

    @Override
    public boolean isUsernameExists(String username, Long excludeUserId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (excludeUserId != null) {
            queryWrapper.ne("id", excludeUserId);
        }
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean isEmailExists(String email, Long excludeUserId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        if (excludeUserId != null) {
            queryWrapper.ne("id", excludeUserId);
        }
        return count(queryWrapper) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone, Long excludeUserId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        if (excludeUserId != null) {
            queryWrapper.ne("id", excludeUserId);
        }
        return count(queryWrapper) > 0;
    }

    @Override
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", count());
        stats.put("active", count(new QueryWrapper<User>().eq("status", "ACTIVE")));
        stats.put("inactive", count(new QueryWrapper<User>().eq("status", "INACTIVE")));
        stats.put("user", count(new QueryWrapper<User>().eq("role", "USER")));
        stats.put("admin", count(new QueryWrapper<User>().eq("role", "ADMIN")));
        return stats;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        // 参数验证
        if (!StringUtils.hasText(email) || !StringUtils.hasText(newPassword)) {
            log.error("重置密码参数不完整");
            return false;
        }
        
        if (newPassword.length() < 6) {
            log.error("新密码长度不能少于6位");
            return false;
        }
        
        User user = getUserByEmail(email);
        if (user == null) {
            log.error("用户不存在，邮箱: {}", email);
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        boolean result = updateById(user);
        
        if (result) {
            log.info("用户密码重置成功，邮箱: {}", email);
        } else {
            log.error("用户密码重置失败，邮箱: {}", email);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 参数验证
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            log.error("密码修改参数不完整");
            return false;
        }
        
        if (newPassword.length() < 6) {
            log.error("新密码长度不能少于6位");
            return false;
        }
        
        User user = getById(userId);
        if (user == null) {
            log.error("用户不存在，ID: {}", userId);
            return false;
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("原密码不匹配，用户ID: {}", userId);
            return false;
        }
        
        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        log.info("新密码(加密后): {}", encodedNewPassword);
        
        // 更新用户密码
        user.setPassword(encodedNewPassword);
        boolean result = updateById(user);
        
        if (result) {
            log.info("用户密码修改成功，用户ID: {}", userId);
            
            // 验证密码是否真的更新到数据库
            User updatedUser = getById(userId);
            boolean passwordUpdated = passwordEncoder.matches(newPassword, updatedUser.getPassword());
            log.info("密码更新验证结果: {}", passwordUpdated);
            
            if (!passwordUpdated) {
                log.error("密码更新验证失败，用户ID: {}", userId);
                return false;
            }
        } else {
            log.error("用户密码修改失败，用户ID: {}", userId);
        }
        
        return result;
    }
}