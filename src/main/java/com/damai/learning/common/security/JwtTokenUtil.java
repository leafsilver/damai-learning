package com.damai.learning.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT创建和解析工具。
 */
public final class JwtTokenUtil {

    private static final int HS256_MIN_KEY_BYTES = 32;

    private JwtTokenUtil() {
    }

    /**
     * 创建用户Access Token。
     */
    public static String createToken(
            Long userId,
            String username,
            String secret,
            long expirationMinutes
    ) {
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("JWT有效期必须大于0");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(
                expirationMinutes * 60
        );

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(createSecretKey(secret))
                .compact();
    }

    /**
     * 验证并解析JWT。
     */
    public static Claims parseToken(
            String token,
            String secret
    ) {
        return Jwts.parser()
                .verifyWith(createSecretKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 根据本地文本创建HS256签名密钥。
     */
    private static SecretKey createSecretKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < HS256_MIN_KEY_BYTES) {
            throw new IllegalArgumentException(
                    "HS256的JWT密钥不能少于32字节"
            );
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}