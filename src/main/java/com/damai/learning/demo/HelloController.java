package com.damai.learning.demo;

import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import com.damai.learning.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "基础接口")
@RestController
@RequestMapping("/api")
public class HelloController {

    @Operation(summary = "检查服务是否运行")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("damai-learning is running");
    }

    @Operation(summary = "测试统一业务异常")
    @GetMapping("/hello/error")
    public Result<Void> error() {
        throw new BusinessException(
                CommonErrorCode.INVALID_PARAMETER
        );
    }
}