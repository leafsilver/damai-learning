package com.damai.learning.order.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单支付成功消息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidMessage {

    /** 消息唯一标识。 */
    private String messageId;

    /** 已支付订单号。 */
    private String orderNo;

    /** 支付时间。 */
    private LocalDateTime paidTime;
}