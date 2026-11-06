package com.damai.learning.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.order.entity.TicketOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 票务订单数据访问接口。
 */
@Mapper
public interface TicketOrderMapper extends BaseMapper<TicketOrder> {
}