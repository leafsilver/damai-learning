package com.damai.learning.demo;

import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目基础能力演示接口，用于验证服务、参数校验和统一异常处理。
 */
@Tag(name = "基础接口")
@RestController
@RequestMapping("/api")
public class HelloController {

    @Operation(summary = "检查服务是否运行")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("damai-learning is running");
    }

    @Operation(summary = "测试请求参数校验")
    @PostMapping("/hello")
    public Result<String> hello(@Valid @RequestBody HelloRequest request) {
        return Result.success("hello, " + request.getName());
    }

    @Operation(summary = "测试统一业务异常")
    @GetMapping("/hello/error")
    public Result<Void> error() {
        throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
    }
}
