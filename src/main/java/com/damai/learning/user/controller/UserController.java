package com.damai.learning.user.controller;

import com.damai.learning.common.result.Result;
import com.damai.learning.user.dto.UserLoginRequest;
import com.damai.learning.user.dto.UserRegisterRequest;
import com.damai.learning.user.service.UserService;
import com.damai.learning.user.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        return Result.success(
                userService.register(request)
        );
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(
            @Valid @RequestBody UserLoginRequest request
    ) {
        return Result.success(
                userService.login(request)
        );
    }
}