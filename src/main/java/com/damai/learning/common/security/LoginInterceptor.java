package com.damai.learning.common.security;

import com.damai.learning.common.exception.BusinessException;
import com.damai.learning.common.exception.CommonErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录认证拦截器。
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    /**
     * Controller执行前验证JWT。
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String authorization = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        if (authorization == null
                || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(
                    CommonErrorCode.LOGIN_REQUIRED
            );
        }

        String token = authorization
                .substring(BEARER_PREFIX.length())
                .trim();

        if (token.isEmpty()) {
            throw new BusinessException(
                    CommonErrorCode.LOGIN_REQUIRED
            );
        }

        try {
            Claims claims = JwtTokenUtil.parseToken(
                    token,
                    jwtSecret
            );

            Long userId = Long.valueOf(
                    claims.getSubject()
            );

            LoginUserContext.setUserId(userId);

            return true;
        } catch (JwtException | NumberFormatException exception) {
            throw new BusinessException(
                    CommonErrorCode.TOKEN_INVALID_OR_EXPIRED
            );
        }
    }

    /**
     * 请求结束后清理登录上下文。
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception
    ) {
        LoginUserContext.clear();
    }
}