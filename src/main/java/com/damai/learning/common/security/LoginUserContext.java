package com.damai.learning.common.security;

import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;

/**
 * 保存当前请求的登录用户信息。
 */
public final class LoginUserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER =
            new ThreadLocal<>();

    private LoginUserContext() {
    }

    /**
     * 保存当前登录用户id。
     */
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前登录用户id。
     */
    public static Long getUserId() {
        Long userId = USER_ID_HOLDER.get();

        if (userId == null) {
            throw new BusinessException(
                    CommonErrorCode.LOGIN_REQUIRED
            );
        }

        return userId;
    }

    /**
     * 清除当前线程保存的用户信息。
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}