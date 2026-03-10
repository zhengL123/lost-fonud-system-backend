package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("claim_records")
public class ClaimRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_id")
    private Long itemId;

    @TableField("claim_user_id")
    private Long claimUserId;

    @TableField("claim_reason")
    private String claimReason;

    @TableField("contact_info")
    private String contactInfo;

    @TableField("status")
    private String status; // "PENDING", "APPROVED", "REJECTED", "COMPLETED"

    @TableField("admin_remark")
    private String adminRemark;

    @TableField("processed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;

    // 关联对象（非数据库字段）
    @TableField(exist = false)
    private String itemName;

    @TableField(exist = false)
    private String itemCategory;

    @TableField(exist = false)
    private String claimUserName;

    @TableField(exist = false)
    private String creatorName;

    @TableField(exist = false)
    private String itemDescription;

    @TableField(exist = false)
    private String lostLocation;

    @TableField(exist = false)
    private String lostTime;

    @TableField(exist = false)
    private String itemImage;
}