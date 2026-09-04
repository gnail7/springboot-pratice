package com.example.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：接口访问日志 + 注册 JWT 拦截器 + 跨域（CORS）配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiLogInterceptor apiLogInterceptor;

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(ApiLogInterceptor apiLogInterceptor, JwtInterceptor jwtInterceptor) {
        this.apiLogInterceptor = apiLogInterceptor;
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * 跨域配置：暂时允许所有来源访问，方便前后端联调。
     * 上线前请收紧，改为具体的域名白名单。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源（配合 allowCredentials 使用 allowedOriginPatterns）
                .allowedOriginPatterns("*")
                // 允许所有 HTTP 方法（GET/POST/PUT/DELETE/OPTIONS 等）
                .allowedMethods("*")
                // 允许所有请求头（包含 Authorization: Bearer xxx）
                .allowedHeaders("*")
                // 允许携带 Cookie / 凭证
                .allowCredentials(true)
                // 预检请求缓存时间（秒），减少 OPTIONS 请求次数
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 接口访问日志：所有请求都记录
        registry.addInterceptor(apiLogInterceptor)
                .addPathPatterns("/**");

        // 2. JWT 登录校验：拦截 /api/**（AuthController/UserController/RoleController 都挂在 /api 下）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                        "/api/**"
                )
                // 登录/注册接口本身不需要 token
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register"
                );
    }
}
