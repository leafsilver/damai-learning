package com.damai.learning.common.exception;

public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(40001, "请求参数错误"),
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