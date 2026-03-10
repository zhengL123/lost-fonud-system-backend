package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.Message;
import com.lostfound.server.mapper.MessageMapper;
import com.lostfound.server.service.MessageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    /**
     * 根据接收者ID获取消息列表
     */
    @Override
    public Page<Message> getMessagesByReceiverId(Long receiverId, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectByReceiverId(page, receiverId);
    }

    /**
     * 根据发送者ID获取消息列表
     */
    @Override
    public Page<Message> getMessagesBySenderId(Long senderId, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectBySenderId(page, senderId);
    }

    @Override
    public List<Message> getMessagesByType(String msgType) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getMsgType, msgType)
                .orderByDesc(Message::getCreatedTime);
        return list(wrapper);
    }

    @Override
    public long getUnreadMessageCount(Long receiverId) {
        return baseMapper.countUnreadMessages(receiverId);
    }

    /**
     * 标记消息为已读
     */
    @Override
    public boolean markAsRead(Long messageId) {
        return baseMapper.markAsRead(messageId) > 0;
    }

    /**
     * 获取用户间的对话
     */
    @Override
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return baseMapper.selectConversation(user1Id, user2Id);
    }

    /**
     * 批量标记消息为已读
     */
    @Override
    public boolean markAllAsRead(Long receiverId) {
        List<Message> unreadMessages = baseMapper.selectUnreadMessages(receiverId);
        if (unreadMessages.isEmpty()) {
            return true;
        }
        
        List<Long> messageIds = unreadMessages.stream()
                .map(Message::getId)
                .collect(java.util.stream.Collectors.toList());
        
        return baseMapper.batchMarkAsRead(messageIds) > 0;
    }
}