package com.damai.learning.order.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息消费记录实体，对应d_message_consume_record。
 */
@Data
@TableName("d_message_consume_record")
public class MessageConsumeRecord {

    /** 主键id。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 消息唯一标识。 */
    private String messageId;

    /** 消息Topic。 */
    private String topic;

    /** 业务编号，当前保存订单号。 */
    private String businessNo;

    /** 消费时间。 */
    private LocalDateTime consumeTime;
}