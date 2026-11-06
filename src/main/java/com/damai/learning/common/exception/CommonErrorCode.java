package com.damai.learning.common.exception;

/**
 * 项目通用错误码。
 */
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(40001, "请求参数错误"),

    PROGRAM_NOT_EXIST(41001, "节目不存在"),

    PROGRAM_CANNOT_OFF_SALE(41002, "节目不存在或已下架"),

    TICKET_CATEGORY_NOT_AVAILABLE(42001, "票档不存在或不可售"),

    PROGRAM_SESSION_NOT_AVAILABLE(42002, "场次不存在或不可售"),

    PROGRAM_SESSION_NOT_ON_SALE(42003, "当前不在售票时间内"),

    TICKET_STOCK_NOT_ENOUGH(42004, "票档库存不足"),

    ORDER_CREATE_FAILED(43001, "订单创建失败"),

    ORDER_NOT_EXIST(43002, "订单不存在"),

    ORDER_STATUS_NOT_ALLOWED(43003, "当前订单状态不允许取消"),

    ORDER_CANCEL_FAILED(43004, "订单取消失败"),

    REQUEST_PATH_NOT_FOUND(40400, "请求地址不存在"),

    INTERNAL_SERVER_ERROR(50000, "系统异常");

    private final int code;

    private final String message;

    CommonErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
