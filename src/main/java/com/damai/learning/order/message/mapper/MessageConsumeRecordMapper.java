package com.damai.learning.order.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damai.learning.order.message.entity.MessageConsumeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息消费记录数据访问接口。
 */
@Mapper
public interface MessageConsumeRecordMapper
        extends BaseMapper<MessageConsumeRecord> {
}