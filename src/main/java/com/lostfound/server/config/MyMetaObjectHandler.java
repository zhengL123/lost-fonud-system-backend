package com.lostfound.server.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 
 * 处理实体类中标记了 @TableField(fill = FieldFill.INSERT) 
 * 和 @TableField(fill = FieldFill.INSERT_UPDATE) 的字段自动填充
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据时的填充策略
     * 
     * 在执行插入操作时，自动填充创建时间和更新时间
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        // 填充更新时间
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新数据时的填充策略
     * 
     * 在执行更新操作时，自动填充更新时间
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}