package com.example.springboot.exception;

/**
 * 业务异常
 *
 * <p>业务代码中遇到"可预期的、需要告诉前端的"错误时抛出，
 * 例如：用户不存在、参数不合法、权限不足等。</p>
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
