package com.damai.learning.order.enums;

/**
 * 订单状态。
 */
public enum OrderStatus {

    PENDING_PAYMENT(1, "待支付"),

    PAID(2, "已支付"),

    CANCELED(3, "已取消");

    private final int code;

    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}