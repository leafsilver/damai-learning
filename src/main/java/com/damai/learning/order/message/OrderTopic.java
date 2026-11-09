package com.damai.learning.order.message;

/**
 * 订单相关Kafka Topic。
 */
public final class OrderTopic {

    /** 订单支付成功Topic。 */
    public static final String ORDER_PAID =
            "damai-order-paid";

    private OrderTopic() {
    }
}