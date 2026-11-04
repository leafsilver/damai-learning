package com.damai.learning.common.exception;

import com.damai.learning.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldError> fieldErrors =
                exception.getBindingResult().getFieldErrors();

        if (fieldErrors.isEmpty()) {
            return Result.failure(
                    CommonErrorCode.INVALID_PARAMETER
            );
        }

        String message = fieldErrors.get(0).getDefaultMessage();

        return Result.failure(
                CommonErrorCode.INVALID_PARAMETER,
                message
        );
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("系统发生未处理异常", exception);
        return Result.failure(
                CommonErrorCode.INTERNAL_SERVER_ERROR
        );
    }
}