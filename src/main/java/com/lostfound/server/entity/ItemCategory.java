package com.lostfound.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("item_categories")
public class ItemCategory{
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category_name")
    private String categoryName;

    @TableField("description")
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;
    /* 插入和更新数据时自动填充fill = FieldFill.INSERT*/
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}