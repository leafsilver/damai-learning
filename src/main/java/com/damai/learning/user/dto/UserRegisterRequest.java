package com.damai.learning.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求参数。
 */
@Data
public class UserRegisterRequest {

    /** 登录用户名，只允许字母、数字和下划线。 */
    @NotBlank(message = "用户名不能为空")
    @Size(
            min = 4,
            max = 20,
            message = "用户名长度必须为4到20位"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "用户名只能包含字母、数字和下划线"
    )
    private String username;

    /** 登录密码。 */
    @NotBlank(message = "密码不能为空")
    @Size(
            min = 8,
            max = 64,
            message = "密码长度必须为8到64位"
    )
    private String password;

    /** 用户昵称。 */
    @NotBlank(message = "昵称不能为空")
    @Size(
            max = 30,
            message = "昵称不能超过30个字符"
    )
    private String nickname;
}