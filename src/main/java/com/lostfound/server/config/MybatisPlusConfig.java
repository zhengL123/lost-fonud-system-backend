package com.lostfound.server.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 功能：配置 MyBatis-Plus 的核心插件，包括分页、乐观锁、防止全表操作等
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 插件集合
     * @return MybatisPlusInterceptor 实例，包含所有已配置的插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L);       // 单页最大记录数
        paginationInterceptor.setOptimizeJoin(true);   // 优化 COUNT JOIN 查询（仅对 LEFT JOIN 有效）
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 2. 乐观锁插件（需在实体类字段上加 @Version 注解）
        // 适用场景：高并发下保证数据一致性（如库存扣减）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 阻断全表更新/删除插件（安全防护）
        // 拦截条件：无 WHERE 条件的 update/delete 操作
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }
}