package com.damai.learning.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Redis Lua脚本配置。
 */
@Configuration
public class RedisScriptConfig {

    /**
     * 加载票档库存扣减Lua脚本。
     */
    @Bean
    public DefaultRedisScript<Long> deductTicketStockScript() {
        DefaultRedisScript<Long> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "lua/deduct_ticket_stock.lua"
                )
        );

        script.setResultType(Long.class);

        return script;
    }

    /**
     * 加载票档库存恢复Lua脚本。
     */
    @Bean
    public DefaultRedisScript<Long> restoreTicketStockScript() {
        DefaultRedisScript<Long> script =
                new DefaultRedisScript<>();

        script.setLocation(
                new ClassPathResource(
                        "lua/restore_ticket_stock.lua"
                )
        );
        script.setResultType(Long.class);

        return script;
    }
}