package com.damai.learning.common.exception;

/**
 * 项目通用错误码。
 */
public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(40001, "请求参数错误"),

    PROGRAM_NOT_EXIST(41001, "节目不存在"),

    PROGRAM_CANNOT_OFF_SALE(41002, "节目不存在或已下架"),

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
