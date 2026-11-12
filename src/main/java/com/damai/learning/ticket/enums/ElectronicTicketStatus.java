package com.damai.learning.ticket.enums;

/**
 * 电子票状态。
 */
public enum ElectronicTicketStatus {

    VALID(1, "有效"),

    USED(2, "已使用"),

    INVALID(3, "已作废");

    private final int code;

    private final String description;

    ElectronicTicketStatus(int code, String description) {
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