package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("thank_notes")
public class ThankNote {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("record_id")
    private Long recordId;

    @TableField("thank_content")
    private String thankContent;

    @TableField("rating")
    private Integer rating;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    // 关联对象（非数据库字段）
    @TableField(exist = false)
    private String claimUserName;

    @TableField(exist = false)
    private String itemName;
}
