package com.lostfound.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;

/**
 * 站内信记录表 Mapper 接口
 * 
 * 对应数据库表：messages
 * 
 * 提供站内信相关的数据访问操作，包括：
 * 1. 基础CRUD操作（继承自BaseMapper）
 * 2. 按发送者/接收者查询消息（支持分页）
 * 3. 查询用户间的对话记录
 * 4. 未读消息查询和统计
 * 5. 消息已读状态管理（单个和批量）

 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 根据接收者ID查询消息
     * 
     * 查询指定用户接收的所有消息，支持分页显示
     * 关联查询发送者和接收者的用户名，方便前端显示
     * 查询结果按创建时间降序排序，最新消息排在前面
     * 
     * @param page 分页对象，包含当前页码和每页大小等信息
     * @param receiverId 接收者ID，必须是已存在的用户ID
     * @return 消息分页对象，包含消息列表和分页信息
     */
    @Results({
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "receiverName", column = "receiver_name")
    })
    @Select("SELECT m.*, u1.username as sender_name, u2.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users u1 ON m.sender_id = u1.id " +
            "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
            "WHERE m.receiver_id = #{receiverId} " +
            "ORDER BY m.created_time DESC")
    Page<Message> selectByReceiverId(Page<Message> page, @Param("receiverId") Long receiverId);

    /**
     * 根据发送者ID查询消息
     * 
     * 查询指定用户发送的所有消息，支持分页显示
     * 关联查询发送者和接收者的用户名，方便前端显示
     * 查询结果按创建时间降序排序，最新消息排在前面
     * 
     * @param page 分页对象，包含当前页码和每页大小等信息
     * @param senderId 发送者ID，必须是已存在的用户ID
     * @return 消息分页对象，包含消息列表和分页信息
     */
    @Results({
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "receiverName", column = "receiver_name")
    })
    @Select("SELECT m.*, u1.username as sender_name, u2.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users u1 ON m.sender_id = u1.id " +
            "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
            "WHERE m.sender_id = #{senderId} " +
            "ORDER BY m.created_time DESC")
    Page<Message> selectBySenderId(Page<Message> page, @Param("senderId") Long senderId);

    /**
     * 查询用户间的对话记录
     * 
     * 查询两个用户之间的所有消息记录，不限制发送者和接收者角色
     * 关联查询发送者和接收者的用户名，方便前端显示
     * 查询结果按创建时间升序排序，保持对话的时间顺序
     * 
     * @param user1Id 用户1ID，必须是已存在的用户ID
     * @param user2Id 用户2ID，必须是已存在的用户ID
     * @return 两个用户之间的消息列表，按时间顺序排列，如果没有消息则返回空列表
     */
    @Results({
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "receiverName", column = "receiver_name")
    })
    @Select("SELECT m.*, u1.username as sender_name, u2.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users u1 ON m.sender_id = u1.id " +
            "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
            "WHERE (m.sender_id = #{user1Id} AND m.receiver_id = #{user2Id}) " +
            "OR (m.sender_id = #{user2Id} AND m.receiver_id = #{user1Id}) " +
            "ORDER BY m.created_time ASC")
    List<Message> selectConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * 查询未读消息
     * 
     * 查询指定用户接收的所有未读消息
     * 关联查询发送者和接收者的用户名，方便前端显示
     * 查询结果按创建时间降序排序，最新消息排在前面
     * 通常用于消息中心显示未读消息列表
     * 
     * @param receiverId 接收者ID，必须是已存在的用户ID
     * @return 未读消息列表，按时间降序排列，如果没有未读消息则返回空列表
     */
    @Results({
        @Result(property = "senderName", column = "sender_name"),
        @Result(property = "receiverName", column = "receiver_name")
    })
    @Select("SELECT m.*, u1.username as sender_name, u2.username as receiver_name " +
            "FROM messages m " +
            "LEFT JOIN users u1 ON m.sender_id = u1.id " +
            "LEFT JOIN users u2 ON m.receiver_id = u2.id " +
            "WHERE m.receiver_id = #{receiverId} AND m.is_read = 0 " +
            "ORDER BY m.created_time DESC")
    List<Message> selectUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 统计用户未读消息数量
     * 
     * 统计指定用户接收的未读消息总数
     * 通常用于消息中心显示未读消息数量提示
     * 
     * @param receiverId 接收者ID，必须是已存在的用户ID
     * @return 未读消息数量，如果没有未读消息则返回0
     */
    @Select("SELECT COUNT(*) FROM messages WHERE receiver_id = #{receiverId} AND is_read = 0")
    Long countUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 标记消息为已读
     * 
     * 将指定消息的已读状态设置为已读
     * 通常在用户查看消息详情时调用
     * 
     * @param messageId 消息ID，必须是已存在的消息ID
     * @return 影响的行数，1表示更新成功，0表示消息ID不存在或已经是已读状态
     */
    @Update("UPDATE messages SET is_read = 1 WHERE id = #{messageId}")
    int markAsRead(@Param("messageId") Long messageId);

    /**
     * 批量标记消息为已读
     * 
     * 将多个消息的已读状态设置为已读
     * 通常用于一键标记所有消息为已读功能
     * 使用MyBatis动态SQL，支持任意数量的消息ID
     * 
     * @param messageIds 消息ID列表，不能为空或null
     * @return 影响的行数，表示成功标记为已读的消息数量
     */
    @Update("<script>" +
            "UPDATE messages SET is_read = 1 WHERE id IN " +
            "<foreach item='id' collection='messageIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchMarkAsRead(@Param("messageIds") List<Long> messageIds);
}