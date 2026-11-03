package com.damai.learning.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

    INVALID_PARAMETER(40001, "请求参数错误", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(50000, "系统异常", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;

    private final String message;

    private final HttpStatus httpStatus;

    CommonErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
