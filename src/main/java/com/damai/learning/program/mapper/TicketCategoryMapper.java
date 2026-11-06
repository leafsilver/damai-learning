package com.damai.learning.program.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.program.entity.TicketCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 票档数据访问接口。
 */
@Mapper
public interface TicketCategoryMapper extends BaseMapper<TicketCategory> {

    /**
     * 在库存充足且票档可售时原子扣减库存。
     *
     * @return 影响行数，1表示成功，0表示库存不足或票档不可售
     */
    int deductStock(
            @Param("ticketCategoryId") Long ticketCategoryId,
            @Param("quantity") Integer quantity
    );

    /**
     * 取消订单时恢复可用库存。
     *
     * @return 影响行数，1表示恢复成功
     */
    int restoreStock(
            @Param("ticketCategoryId") Long ticketCategoryId,
            @Param("quantity") Integer quantity
    );
}