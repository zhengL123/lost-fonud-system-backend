package com.lostfound.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lostfound.server.entity.Message;
import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 根据接收者ID查询消息
     * @param receiverId 接收者ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 消息分页对象
     */
    Page<Message> getMessagesByReceiverId(Long receiverId, Integer pageNum, Integer pageSize);

    /**
     * 根据发送者ID查询消息
     * @param senderId 发送者ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 消息分页对象
     */
    Page<Message> getMessagesBySenderId(Long senderId, Integer pageNum, Integer pageSize);

    /**
     * 根据消息类型查询消息
     * @param msgType 消息类型
     * @return 消息列表
     */
    List<Message> getMessagesByType(String msgType);

    /**
     * 获取未读消息数量
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    long getUnreadMessageCount(Long receiverId);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Long messageId);

    /**
     * 获取用户间的对话
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 消息列表
     */
    List<Message> getConversation(Long user1Id, Long user2Id);

    /**
     * 批量标记消息为已读
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean markAllAsRead(Long receiverId);
}