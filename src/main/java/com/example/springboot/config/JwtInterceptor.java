package com.example.springboot.config;

import com.example.springboot.utils.JwtUtil;
import com.example.springboot.exception.BusinessException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器
 *
 * <p>校验请求头 Authorization: Bearer xxx，通过后把 userId / username
 * 放入 request attribute，Controller 里用 @RequestAttribute 取出。</p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 只拦截 Controller 方法（放过静态资源等）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录");
        }

        try {
            Claims claims = jwtUtil.parseToken(authorization.substring(7));
            request.setAttribute("userId", Long.valueOf(claims.getSubject()));
            request.setAttribute("username", claims.get("username", String.class));
            return true;
        } catch (Exception e) {
            // token 过期 / 被篡改 / 格式错误，统一按 401 处理
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }
}
