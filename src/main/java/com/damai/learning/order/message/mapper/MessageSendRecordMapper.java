package com.damai.learning.order.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.order.message.entity.MessageSendRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送记录数据访问接口。
 */
@Mapper
public interface MessageSendRecordMapper
        extends BaseMapper<MessageSendRecord> {
}