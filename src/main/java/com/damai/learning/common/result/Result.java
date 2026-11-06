package com.damai.learning.common.result;

import com.damai.learning.common.exception.ErrorCode;

/**
 * 接口统一响应结构。
 *
 * @param <T> 响应数据类型
 */
public class Result<T> {

    /** 业务状态码，0表示成功。 */
    private final int code;

    /** 响应说明。 */
    private final String message;

    /** 业务数据。 */
    private final T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 创建带业务数据的成功响应。 */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    /** 创建不带业务数据的成功响应。 */
    public static Result<Void> success() {
        return new Result<>(0, "success", null);
    }

    /** 根据错误码创建失败响应。 */
    public static <T> Result<T> failure(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /** 根据错误码和自定义提示创建失败响应。 */
    public static <T> Result<T> failure(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
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
