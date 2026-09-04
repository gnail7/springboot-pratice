package com.example.springboot.controller;

import com.example.springboot.dto.login.RegisterDTO;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.Result;
import com.example.springboot.vo.login.LoginUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录 / 注册 / 获取当前登录用户
 */
@RestController
@Tag(name = "认证管理")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户登录", description = "用户通过用户名和密码登录，登录成功后返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto) {
        this.userService.register(dto);
        return Result.success();
    }

    /**
     * 获取当前登录用户（需要带 Authorization: Bearer xxx）。
     * token 已由 JwtInterceptor 校验，userId / username 被放入 request attribute。
     * 返回：用户信息 + 拥有的角色 + 角色的菜单权限标识(perms)
     */
    @Operation(summary = "获取当前登录用户信息、角色与权限")
    @GetMapping("/me")
    public Result<LoginUserVO> me(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getLoginUser(userId));
    }

    /** 登录请求参数 */
    @Data
    public static class LoginDTO {

        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    /** 登录成功返回结果 */
    @Data
    public static class LoginVO {

        /** JWT token */
        private String token;

        /** 用户信息（password 字段已被 @JsonIgnore 忽略，不会返回） */
        private User user;
    }
}
