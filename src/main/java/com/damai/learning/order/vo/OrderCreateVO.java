package com.damai.learning.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单响应。
 */
@Data
public class OrderCreateVO {

    /** 订单主键。 */
    private Long orderId;

    /** 订单号。 */
    private String orderNo;

    /** 订单总金额。 */
    private BigDecimal totalAmount;

    /** 订单状态：1待支付。 */
    private Integer orderStatus;
}