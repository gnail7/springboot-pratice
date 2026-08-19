package com.example.springboot.exception;

import com.example.springboot.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理器
 *
 * <p>监听所有 Controller 抛出的异常，统一转换成 {@link Result} 格式返回，
 * 避免每个接口各自 try-catch。</p>
 * slf4j 这是 Lombok 提供的注解，作用是自动帮你生成一个日志对象。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常：返回业务自己定义的 code 和 message
     * <p>例如：用户不存在 → 404；没有登录 → 401；没有权限 → 403</p>
     */
    @ExceptionHandler(BusinessException.class)  // 只接 BusinessException
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常：@Valid 校验失败时抛出（参数错误 → 400）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : e.getMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 兜底异常：所有未被上面捕获的异常都走这里 → 500
     */
    @ExceptionHandler(Exception.class)  // 接所有异常
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, e.getMessage());
    }
}
