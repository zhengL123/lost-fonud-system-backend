package com.lostfound.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 失物招领数据传输对象
 * 
 * 用于接收前端提交的失物招领信息，包括：
 * 1. 物品基本信息 - 名称、描述、类别
 * 2. 丢失信息 - 地点、时间
 * 3. 联系信息 - 联系方式
 * 4. 图片信息 - 多张图片URL列表
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class LostItemDTO {
    
    /**
     * 物品名称
     */
    private String itemName;
    
    /**
     * 物品描述
     */
    private String itemDescription;
    
    /**
     * 物品描述（兼容字段）
     */
    private String description;
    
    /**
     * 物品类别ID
     */
    private Long categoryId;
    
    /**
     * 丢失地点
     */
    private String lostLocation;
    
    /**
     * 丢失时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lostTime;
    
    /**
     * 联系方式
     */
    private String contactInfo;
    
    /**
     * 物品状态：0-未找回，1-已找回，2-已认领
     */
    private Integer status = 0;
    
    /**
     * 物品图片URL列表
     * 存储多张图片的URL，最多5张
     */
    private List<String> itemImages;
    
    /**
     * 创建者ID
     */
    private Long creatorId;
    
    /**
     * 设置图片URL列表
     * 
     * @param imageUrls 图片URL列表
     */
    public void setItemImages(List<String> imageUrls) {
        this.itemImages = imageUrls;
    }
}