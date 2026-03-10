package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.entity.Message;
import com.lostfound.server.service.MessageService;
import com.lostfound.server.util.PageResult;
import com.lostfound.server.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public Result<String> sendMessage(@RequestBody Message message) {
        boolean saved = messageService.save(message);
        return saved ? Result.success("发送成功") : Result.error("发送失败");
    }

    /**
     * 分页查询消息
     */
    @GetMapping("/page")
    public Result<PageResult<Message>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Message> page = new Page<>(pageNum, pageSize);
        Page<Message> result = messageService.page(page);
        // 转换为自定义分页响应格式
        PageResult<Message> pageResult = PageResult.of(result);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询消息详情
     */
    @GetMapping("/{id}")
    public Result<Message> getById(@PathVariable Long id) {
        Message message = messageService.getById(id);
        return message != null ? Result.success(message) : Result.error("消息不存在");
    }

    /**
     * 更新消息
     */
    @PutMapping("/{id}")
    public Result<String> updateById(@PathVariable Long id, @RequestBody Message message) {
        message.setId(id);
        boolean updated = messageService.updateById(message);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        boolean removed = messageService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据接收者ID查询消息
     */
    @GetMapping("/receiver/{receiverId}")
    public Result<PageResult<Message>> getByReceiverId(
            @PathVariable Long receiverId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Message> messages = messageService.getMessagesByReceiverId(receiverId, pageNum, pageSize);
        // 转换为自定义分页响应格式
        PageResult<Message> pageResult = PageResult.of(messages);
        return Result.success(pageResult);
    }

    /**
     * 根据发送者ID查询消息
     */
    @GetMapping("/sender/{senderId}")
    public Result<PageResult<Message>> getBySenderId(
            @PathVariable Long senderId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Message> messages = messageService.getMessagesBySenderId(senderId, pageNum, pageSize);
        // 转换为自定义分页响应格式
        PageResult<Message> pageResult = PageResult.of(messages);
        return Result.success(pageResult);
    }

    /**
     * 查询未读消息数量
     */
    @GetMapping("/unread/count/{receiverId}")
    public Result<Long> getUnreadCount(@PathVariable Long receiverId) {
        long count = messageService.getUnreadMessageCount(receiverId);
        return Result.success(count);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    public Result<String> markAsRead(@PathVariable Long id) {
        boolean updated = messageService.markAsRead(id);
        return updated ? Result.success("标记成功") : Result.error("标记失败");
    }

    /**
     * 查询用户间的对话
     */
    @GetMapping("/conversation")
    public Result<List<Message>> getConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        List<Message> messages = messageService.getConversation(user1Id, user2Id);
        return Result.success(messages);
    }

    /**
     * 批量标记消息为已读
     */
    @PutMapping("/read-all/{receiverId}")
    public Result<String> markAllAsRead(@PathVariable Long receiverId) {
        boolean updated = messageService.markAllAsRead(receiverId);
        return updated ? Result.success("标记成功") : Result.error("标记失败");
    }
}