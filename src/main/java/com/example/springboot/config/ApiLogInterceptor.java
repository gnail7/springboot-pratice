package com.example.springboot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 全局接口访问日志拦截器
 *
 * <p>自动打印所有 Controller 接口的：请求方式/路径/来源IP/请求参数/处理方法，
 * 以及请求结束后的状态码与耗时。静态资源等非接口请求不记录。</p>
 *
 * <p>说明：POST 的 JSON body 无法从参数表读取，需要的话可在各 Service 里按需打印；
 * 出于安全考虑不打印 Authorization 等请求头。</p>
 */
@Slf4j
@Component
public class ApiLogInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 只记录真正的 Controller 接口（静态资源等 handler 不是 HandlerMethod，跳过）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        START_TIME.set(System.currentTimeMillis());

        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String requestLine = uri + (query == null ? "" : "?" + query);

        log.info("========== 接口请求开始 ==========");
        log.info("请求: {} {}", request.getMethod(), requestLine);
        log.info("来源IP: {}", getClientIp(request));
        log.info("请求参数: {}", buildParams(request));
        log.info("处理方法: {}.{}()",
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        Long start = START_TIME.get();
        if (start == null) {
            // 非 Controller 请求，preHandle 未计时，不记录
            return;
        }

        long cost = System.currentTimeMillis() - start;
        String requestLine = request.getMethod() + " " + request.getRequestURI();
        int status = response.getStatus();

        if (ex != null) {
            log.error("接口异常结束: {} -> 状态码 {}，耗时 {}ms，异常: {}",
                    requestLine, status, cost, ex.toString());
        } else {
            log.info("接口正常结束: {} -> 状态码 {}，耗时 {}ms", requestLine, status, cost);
        }
        log.info("========== 接口请求结束 ==========");

        START_TIME.remove();
    }

    /** 取客户端 IP（兼容经过代理的场景） */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /** 拼接请求参数（GET 查询串/表单参数；POST JSON body 不在参数表中） */
    private String buildParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return "(无查询参数，POST body 请见 Service 日志)";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sb.append(entry.getKey()).append('=')
                    .append(String.join(",", entry.getValue())).append(' ');
        }
        return sb.toString().trim();
    }
}
