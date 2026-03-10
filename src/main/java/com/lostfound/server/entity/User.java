package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 对应数据库表 users，存储系统用户信息
 * 包含用户基本信息、角色权限、状态等字段
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
@TableName("users")
public class User {
    /**
     * 用户ID
     * 主键，自增长
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     * 用于登录和显示的用户名称
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     * 用户登录密码，存储加密后的值
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     * 用户的邮箱地址，可用于找回密码
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     * 用户的手机号码，可用于找回密码
     */
    @TableField("phone")
    private String phone;

    /**
     * 用户角色
     * 用户在系统中的角色，如USER（普通用户）、ADMIN（管理员）
     */
    @TableField("role")
    private String role;

    /**
     * 头像
     * 用户头像的URL地址
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 创建时间
     * 用户账号的创建时间，插入时自动填充
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     * 用户信息的最后更新时间，插入和更新时自动填充
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 用户状态
     * 用户账号的状态，如ACTIVE（活跃）、INACTIVE（非活跃）
     */
    @TableField("status")
    private String status;

    /**
     * 默认构造函数
     */
    public User() {
    }

    /**
     * 带参构造函数
     * 
     * 创建一个新用户，设置基本属性
     * 
     * @param username 用户名
     * @param password 密码
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER";  // 默认角色为普通用户
        this.status = "ACTIVE";  // 默认状态为活跃
    }

    /**
     * 判断用户是否为管理员
     * 
     * @return boolean true-是管理员，false-不是管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /**
     * 判断用户是否处于活跃状态
     * 
     * @return boolean true-活跃状态，false-非活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * 获取用户显示名称
     * 
     * 优先返回用户名，如果用户名为空则返回邮箱，如果邮箱也为空则返回手机号
     * 如果以上都为空，则返回"未知用户"
     * 
     * @return String 用户显示名称
     */
    public String getDisplayName() {
        if (username != null && !username.trim().isEmpty()) {
            return username;
        } else if (email != null && !email.trim().isEmpty()) {
            return email;
        } else if (phone != null && !phone.trim().isEmpty()) {
            return phone;
        }
        return "未知用户";
    }

    /**
     * 获取用户头像，如果用户没有设置头像则返回默认头像
     * 
     * @return String 头像URL地址
     */
    public String getAvatarOrDefault() {
        if (avatar != null && !avatar.trim().isEmpty()) {
            return avatar;
        }
        return generateDefaultAvatar();
    }

    /**
     * 生成默认头像
     * 
     * 根据用户名的首字符生成对应颜色的默认头像
     * 如果用户名为空，则返回默认头像
     * 
     * @return String 默认头像URL地址
     */
    private String generateDefaultAvatar() {
        if (username == null || username.trim().isEmpty()) {
            return "/avatars/default.jpg";
        }

        // 根据用户名首字符计算颜色索引
        char firstChar = username.charAt(0);
        int colorIndex = Math.abs(firstChar) % 6;
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD"};

        return String.format("/avatars/default-%d.jpg", colorIndex);
    }

    /**
     * 验证用户信息是否有效
     * 
     * 检查用户名、密码、角色和状态是否都不为空
     * 
     * @return boolean true-用户信息有效，false-用户信息无效
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                role != null && !role.trim().isEmpty() &&
                status != null && !status.trim().isEmpty();
    }

    /**
     * 用户简单视图对象
     * 
     * 用于返回给前端的用户信息，不包含敏感字段如密码
     * 适用于用户列表展示、用户信息展示等场景
     * 
     * 包含字段：
     * - id: 用户ID
     * - username: 用户名
     * - email: 邮箱
     * - phone: 手机号
     * - role: 用户角色
     * - avatar: 头像
     * - status: 用户状态
     * - createdTime: 创建时间
     */
    @Data
    public static class UserSimpleVO {
        /**
         * 用户ID
         * 用户在系统中的唯一标识
         */
        private Long id;
        
        /**
         * 用户名
         * 用户登录和显示的名称
         */
        private String username;
        
        /**
         * 邮箱
         * 用户的邮箱地址，可用于找回密码
         */
        private String email;
        
        /**
         * 手机号
         * 用户的手机号码，可用于找回密码
         */
        private String phone;
        
        /**
         * 用户角色
         * 用户在系统中的角色，如USER、ADMIN
         */
        private String role;
        
        /**
         * 头像
         * 用户头像的URL地址
         */
        private String avatar;
        
        /**
         * 用户状态
         * 用户账号的状态，如ACTIVE（活跃）、INACTIVE（非活跃）
         */
        private String status;
        
        /**
         * 创建时间
         * 用户账号的创建时间
         */
        private LocalDateTime createdTime;
    }
}