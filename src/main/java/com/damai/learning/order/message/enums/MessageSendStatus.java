package com.damai.learning.order.message.enums;

/**
 * 消息发送状态。
 */
public enum MessageSendStatus {

    PENDING(0, "待发送"),

    SENT(1, "已发送");

    private final int code;

    private final String description;

    MessageSendStatus(
            int code,
            String description
    ) {
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