package com.lostfound.server.util;

import lombok.Data;

import java.util.List;

/**
 * 自定义分页响应类
 * 
 * 用于统一分页查询的响应格式，符合需求文档中的分页响应格式要求
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Data
public class PageResult<T> {
    
    /**
     * 当前页数据列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 每页数量
     */
    private Long size;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 构造函数
     * 
     * @param records 当前页数据列表
     * @param total 总记录数
     * @param size 每页数量
     * @param current 当前页码
     */
    public PageResult(List<T> records, Long total, Long size, Long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        // 计算总页数
        this.pages = (total + size - 1) / size;
    }
    
    /**
     * 从MyBatis-Plus的Page对象转换为自定义PageResult
     * 
     * @param page MyBatis-Plus的Page对象
     * @return 自定义PageResult对象
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return new PageResult<>(
            page.getRecords(),
            page.getTotal(),
            (long) page.getSize(),
            (long) page.getCurrent()
        );
    }
}