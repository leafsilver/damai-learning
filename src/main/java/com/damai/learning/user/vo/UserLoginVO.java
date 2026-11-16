package com.damai.learning.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录响应。
 */
@Data
@AllArgsConstructor
public class UserLoginVO {

    /** JWT Access Token。 */
    private String accessToken;

    /** Token使用方式。 */
    private String tokenType;

    /** Token剩余有效时间，单位：秒。 */
    private long expiresIn;
}
