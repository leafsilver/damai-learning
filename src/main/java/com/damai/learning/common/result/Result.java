package com.damai.learning.common.result;

import com.damai.learning.common.exception.ErrorCode;

public class Result<T> {

    private final int code;

    private final String message;

    private final T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> failure(ErrorCode errorCode) {
        return new Result<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}