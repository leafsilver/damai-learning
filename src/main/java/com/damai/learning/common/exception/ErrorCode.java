package com.damai.learning.common.exception;

/**
 * 错误码统一规范，业务模块可以通过实现该接口扩展自己的错误码。
 */
public interface ErrorCode {

    int getCode();

    String getMessage();
}
