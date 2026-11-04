package com.damai.learning.common.exception;

import com.damai.learning.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(
            BusinessException exception
    ) {
        return Result.failure(exception.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("系统发生未处理异常", exception);
        return Result.failure(
                CommonErrorCode.INTERNAL_SERVER_ERROR
        );
    }
}