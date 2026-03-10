package com.lostfound.server.util;

import com.lostfound.server.entity.LostItem;
import com.lostfound.server.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 失物服务工具类
 * 
 * 提供失物招领相关的辅助功能，包括：
 * 1. 失物信息验证（必填字段检查）
 * 2. 统一响应创建（成功/失败/404等）
 * 3. 分页参数验证
 * 4. 字符串工具方法
 * 
 * 使用@Component注解标记为Spring组件，可在其他类中注入使用
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Component
public class LostItemServiceHelper {
    
    @Autowired
    private LostItemService lostItemService;
    

    public Result validateLostItem(LostItem lostItem) {
        // 检查失物对象是否为空
        if (lostItem == null) {
            return Result.error("失物信息不能为空");
        }
        
        // 检查物品名称是否为空
        if (!StringUtils.hasText(lostItem.getItemName())) {
            return Result.error("物品名称不能为空");
        }
        
        // 检查丢失地点是否为空
        if (!StringUtils.hasText(lostItem.getLostLocation())) {
            return Result.error("丢失地点不能为空");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 创建成功响应
     * 
     * 创建HTTP 200状态码的成功响应，返回消息字符串
     * 
     * @param message 响应消息内容
     * @return ResponseEntity<String> HTTP 200响应，包含消息内容
     * 
     * 使用场景：
     * - 操作成功后返回提示信息
     * - 简单文本响应
     */
    public ResponseEntity<String> createSuccessResponse(String message) {
        return ResponseEntity.ok(message);
    }
    
    /**
     * 创建失败响应
     * 
     * 创建HTTP 400状态码的失败响应，返回错误消息
     * 
     * @param message 错误消息内容
     * @return ResponseEntity<String> HTTP 400响应，包含错误信息
     * 
     * 使用场景：
     * - 参数验证失败
     * - 业务逻辑错误
     * - 客户端请求错误
     */
    public ResponseEntity<String> createFailureResponse(String message) {
        return ResponseEntity.badRequest().body(message);
    }
    
    /**
     * 创建成功响应（带数据）
     * 
     * 创建HTTP 200状态码的成功响应，返回数据对象
     * 
     * @param <T> 数据类型
     * @param data 响应数据内容
     * @return ResponseEntity<T> HTTP 200响应，包含数据对象
     * 
     * 使用场景：
     * - 查询操作返回数据
     * - 需要返回复杂对象或集合
     * - API接口数据返回
     */
    public <T> ResponseEntity<T> createSuccessResponse(T data) {
        return ResponseEntity.ok(data);
    }
    
    /**
     * 创建404响应
     * 
     * 创建HTTP 404状态码的资源未找到响应
     * 
     * @param <T> 预期数据类型（实际无响应体）
     * @return ResponseEntity<T> HTTP 404响应，无响应体
     * 
     * 使用场景：
     * - 查询的资源不存在
     * - 请求的ID对应的数据已删除
     * - URL路径错误
     */
    public <T> ResponseEntity<T> createNotFoundResponse() {
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 处理分页查询
     * 
     * 验证分页参数的有效性，确保分页查询的安全性
     * 
     * @param pageNum 页码，从1开始
     * @param pageSize 每页数量，限制在1-100之间
     * @return Result 验证结果
     *         - 成功：Result.success("验证成功")
     *         - 失败：Result.error("具体错误信息")
     *         
     * 验证规则：
     * 1. 页码必须大于0
     * 2. 每页数量必须在1-100之间（防止过大查询导致性能问题）
     * 
     * 使用场景：
     * - 分页查询前的参数验证
     * - 防止恶意分页参数
     */
    public Result validatePageParams(Integer pageNum, Integer pageSize) {
        // 检查页码是否有效
        if (pageNum == null || pageNum < 1) {
            return Result.error("页码必须大于0");
        }
        
        // 检查每页数量是否在合理范围内
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            return Result.error("每页数量必须在1-100之间");
        }
        
        return Result.success("验证成功");
    }
    
    /**
     * 检查字符串是否为空
     * 
     * 判断字符串是否为null、空字符串或只包含空白字符
     * 
     * @param str 待检查的字符串
     * @return boolean true-字符串为空或null，false-字符串有实际内容
     * 
     * 使用场景：
     * - 条件判断前的空值检查
     * - 可选参数验证
     */
    public boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串是否不为空
     * 
     * 判断字符串是否包含实际内容（非null、非空、非纯空白）
     * 
     * @param str 待检查的字符串
     * @return boolean true-字符串有实际内容，false-字符串为空或null
     * 
     * 使用场景：
     * - 必填参数验证
     * - 字符串操作前的安全检查
     */
    public boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }
}