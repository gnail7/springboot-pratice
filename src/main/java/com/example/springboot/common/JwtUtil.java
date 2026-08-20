package com.example.springboot.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：签发 / 解析 token
 */
@Component
public class JwtUtil {

    /** HS256 密钥（长度必须 >= 32 字节），来自 application.yml */
    @Value("${jwt.secret}")
    private String secret;

    /** token 有效期（毫秒） */
    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 token
     *
     * @param userId   用户 id（作为 subject）
     * @param username 用户名（放入 claim，方便前端/拦截器使用）
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析并校验 token，失败会抛 JwtException
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
