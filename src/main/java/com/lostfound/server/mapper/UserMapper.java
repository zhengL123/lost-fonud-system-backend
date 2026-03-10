package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lostfound.server.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户表 Mapper 接口
 * 
 * 对应数据库表：users
 * 
 * 提供用户相关的数据访问操作，包括：
 * 1. 基础CRUD操作（继承自BaseMapper）
 * 2. 按多种条件查询用户信息（用户名、邮箱、手机号、角色等）
 * 3. 按状态统计用户数量
 * 4. 更新用户信息（密码、最后活跃时间等）
 * 
 * 使用MyBatis-Plus框架，继承BaseMapper获得基础CRUD方法
 * 使用@Select和@Update注解定义SQL语句，简化XML配置
 * 
 * 注意事项：
 * - 所有查询方法默认只返回状态为'ACTIVE'的用户
 * - 密码相关操作需在Service层进行加密处理
 * - 用户状态管理：ACTIVE（活跃）、INACTIVE（非活跃）、LOCKED（锁定）
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     * 
     * 精确匹配用户名，查询指定用户名的用户信息
     * 仅返回状态为'ACTIVE'的活跃用户，过滤掉已删除或禁用的用户
     * 通常用于登录验证和用户名唯一性检查
     * 
     * @param username 用户名，需完全匹配，不区分大小写（取决于数据库配置）
     * @return 用户信息对象，如果不存在或状态非ACTIVE则返回null
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND status = 'ACTIVE'")
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据用户名查询用户信息（包含所有状态）
     * 
     * 精确匹配用户名，查询指定用户名的用户信息
     * 不限制用户状态，用于注册时检查用户名唯一性
     * 
     * @param username 用户名，需完全匹配，不区分大小写（取决于数据库配置）
     * @return 用户信息对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsernameForRegistration(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     * 
     * 精确匹配邮箱地址，查询指定邮箱的用户信息
     * 仅返回状态为'ACTIVE'的活跃用户，过滤掉已删除或禁用的用户
     * 通常用于邮箱登录、找回密码和邮箱唯一性检查
     * 
     * @param email 邮箱地址，需完全匹配，不区分大小写（取决于数据库配置）
     * @return 用户信息对象，如果不存在或状态非ACTIVE则返回null
     */
    @Select("SELECT * FROM users WHERE email = #{email} AND status = 'ACTIVE'")
    User selectByEmail(@Param("email") String email);
    
    /**
     * 根据邮箱查询用户信息（包含所有状态）
     * 
     * 精确匹配邮箱地址，查询指定邮箱的用户信息
     * 不限制用户状态，用于注册时检查邮箱唯一性
     * 
     * @param email 邮箱地址，需完全匹配，不区分大小写（取决于数据库配置）
     * @return 用户信息对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmailForRegistration(@Param("email") String email);

    /**
     * 根据手机号查询用户信息
     * 
     * 精确匹配手机号码，查询指定手机号的用户信息
     * 仅返回状态为'ACTIVE'的活跃用户，过滤掉已删除或禁用的用户
     * 通常用于手机号登录、短信验证和手机号唯一性检查
     * 
     * @param phone 手机号码，需完全匹配，支持国际号码格式（如+86）
     * @return 用户信息对象，如果不存在或状态非ACTIVE则返回null
     */
    @Select("SELECT * FROM users WHERE phone = #{phone} AND status = 'ACTIVE'")
    User selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据手机号查询用户信息（包含所有状态）
     * 
     * 精确匹配手机号码，查询指定手机号的用户信息
     * 不限制用户状态，用于注册时检查手机号唯一性
     * 
     * @param phone 手机号码，需完全匹配，支持国际号码格式（如+86）
     * @return 用户信息对象，如果不存在则返回null
     */
    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User selectByPhoneForRegistration(@Param("phone") String phone);

    /**
     * 根据角色查询用户列表
     * 
     * 精确匹配用户角色，查询指定角色的所有用户信息
     * 仅返回状态为'ACTIVE'的活跃用户，过滤掉已删除或禁用的用户
     * 查询结果按创建时间降序排序，最新注册的用户排在前面
     * 
     * 支持的角色类型：
     * - USER: 普通用户
     * - ADMIN: 管理员
     * - MODERATOR: 版主
     * 
     * @param role 用户角色，需完全匹配，区分大小写
     * @return 指定角色的用户列表，按创建时间降序排列，如果没有匹配项则返回空列表
     */
    @Select("SELECT * FROM users WHERE role = #{role} AND status = 'ACTIVE' ORDER BY created_time DESC")
    List<User> selectByRole(@Param("role") String role);

    /**
     * 查询所有管理员用户
     * 
     * 查询所有角色为'ADMIN'的用户信息
     * 仅返回状态为'ACTIVE'的活跃管理员，过滤掉已删除或禁用的管理员
     * 通常用于管理员权限验证和系统管理功能
     * 
     * @return 管理员用户列表，按创建时间降序排列，如果没有管理员则返回空列表
     */
    @Select("SELECT * FROM users WHERE role = 'ADMIN' AND status = 'ACTIVE'")
    List<User> selectAdmins();

    /**
     * 根据状态查询用户数量
     * 
     * 统计指定状态的用户数量，用于用户状态分析和系统监控
     * 不限制用户角色，包括所有角色的用户
     * 
     * 支持的状态类型：
     * - ACTIVE: 活跃用户
     * - INACTIVE: 非活跃用户
     * - LOCKED: 锁定用户
     * 
     * @param status 用户状态，需完全匹配，区分大小写
     * @return 指定状态的用户数量，如果没有匹配项则返回0
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    Long countByStatus(@Param("status") String status);

    /**
     * 更新用户最后活跃时间
     * 
     * 更新指定用户的最后活跃时间为当前时间
     * 通常在用户登录或进行关键操作时调用，用于跟踪用户活跃度
     * 不影响用户的其他信息和状态
     * 
     * @param userId 用户ID，必须是已存在的用户ID
     * @return 影响的行数，1表示更新成功，0表示用户ID不存在
     */
    @Update("UPDATE users SET updated_time = NOW() WHERE id = #{userId}")
    int updateLastActiveTime(@Param("userId") Long userId);
    
    /**
     * 更新用户密码
     * 
     * 更新指定用户的密码，通常用于密码重置功能
     * 注意：此方法直接更新密码，不进行加密处理
     * 密码加密应在Service层完成后再调用此方法
     * 
     * @param userId 用户ID，必须是已存在的用户ID
     * @param password 加密后的密码字符串，不建议存储明文密码
     * @return 影响的行数，1表示更新成功，0表示用户ID不存在
     */
    @Update("UPDATE users SET password = #{password} WHERE id = #{userId}")
    int updateUserPassword(@Param("userId") Long userId, @Param("password") String password);
}