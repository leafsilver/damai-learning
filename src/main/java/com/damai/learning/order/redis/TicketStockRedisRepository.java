package com.damai.learning.order.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 票档库存Redis访问组件。
 */
@Repository
public class TicketStockRedisRepository {

    private static final String TICKET_STOCK_KEY_PREFIX =
            "damai:ticket:stock:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("deductTicketStockScript")
    private DefaultRedisScript<Long> deductTicketStockScript;

    @Autowired
    @Qualifier("restoreTicketStockScript")
    private DefaultRedisScript<Long> restoreTicketStockScript;

    /**
     * Redis中不存在库存Key时进行初始化。
     *
     * @return true表示本次完成初始化
     */
    public boolean initializeStock(
            Long ticketCategoryId,
            Integer availableStock
    ) {
        Boolean initialized = stringRedisTemplate
                .opsForValue()
                .setIfAbsent(
                        buildKey(ticketCategoryId),
                        availableStock.toString()
                );

        return Boolean.TRUE.equals(initialized);
    }

    /**
     * 使用Lua脚本原子扣减Redis库存。
     *
     * @return 1成功，0库存不足，-1未初始化，-2数量不合法
     */
    public long deductStock(
            Long ticketCategoryId,
            Integer quantity
    ) {
        Long result = stringRedisTemplate.execute(
                deductTicketStockScript,
                List.of(buildKey(ticketCategoryId)),
                quantity.toString()
        );

        if (result == null) {
            throw new IllegalStateException(
                    "Redis库存扣减脚本没有返回结果"
            );
        }

        return result;
    }

    /**
     * 使用Lua脚本原子恢复Redis库存。
     *
     * @return 1恢复成功，0库存Key不存在，-1数量不合法
     */
    public long restoreStock(
            Long ticketCategoryId,
            Integer quantity
    ) {
        Long result = stringRedisTemplate.execute(
                restoreTicketStockScript,
                List.of(buildKey(ticketCategoryId)),
                quantity.toString()
        );

        if (result == null) {
            throw new IllegalStateException(
                    "Redis库存恢复脚本没有返回结果"
            );
        }

        return result;
    }

    /**
     * 查询当前Redis库存。
     */
    public Integer getStock(Long ticketCategoryId) {
        String stock = stringRedisTemplate
                .opsForValue()
                .get(buildKey(ticketCategoryId));

        if (stock == null) {
            return null;
        }

        return Integer.valueOf(stock);
    }

    /**
     * 删除Redis库存，用于后续重新初始化。
     */
    public void deleteStock(Long ticketCategoryId) {
        stringRedisTemplate.delete(
                buildKey(ticketCategoryId)
        );
    }

    private String buildKey(Long ticketCategoryId) {
        return TICKET_STOCK_KEY_PREFIX
                + ticketCategoryId;
    }
}
