package com.damai.learning.order.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息发送记录实体，对应d_message_send_record。
 */
@Data
@TableName("d_message_send_record")
public class MessageSendRecord {

    /** 主键id。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 消息唯一标识。 */
    private String messageId;

    /** 消息Topic。 */
    private String topic;

    /** 消息Key。 */
    private String messageKey;

    /** 消息JSON内容。 */
    private String messageBody;

    /** 发送状态：0待发送，1已发送。 */
    private Integer sendStatus;

    /** 已经重试的次数。 */
    private Integer retryCount;

    /** 最近一次发送失败原因。 */
    private String lastError;

    /** 发送成功时间。 */
    private LocalDateTime sendTime;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 修改时间。 */
    private LocalDateTime editTime;
}