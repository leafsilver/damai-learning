package com.damai.learning.demo;

import com.damai.learning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.success("damai-learning is running");
    }

    @PostMapping("/hello")
    public ApiResponse<String> hello(@Valid @RequestBody HelloRequest request) {
        return ApiResponse.success("你好，" + request.getName());
    }
}
