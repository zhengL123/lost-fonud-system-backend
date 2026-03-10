package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("sender_id")
    private Long senderId;

    @TableField("receiver_id")
    private Long receiverId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("msg_type")
    private String msgType; // "SYSTEM" or "USER"

    @TableField("is_read")
    private Integer isRead; // 0未读, 1已读

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    // 关联对象（非数据库字段）
    @TableField(exist = false)
    private String senderName;

    @TableField(exist = false)
    private String receiverName;
}