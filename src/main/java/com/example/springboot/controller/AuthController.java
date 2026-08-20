package com.example.springboot.controller;

import com.example.springboot.common.JwtUtil;
import com.example.springboot.common.Result;
import com.example.springboot.dto.LoginDTO;
import com.example.springboot.entity.User;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.service.UserService;
import com.example.springboot.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录 / 获取当前用户
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 登录：校验用户名密码，签发 JWT
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {

        // 1. 按用户名查用户
        User user = userService.lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .one();

        // 2. 用户不存在或密码错误（统一提示，防止暴露"用户是否存在"）
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 3. 签发 token 并返回
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(user);
        return Result.success(vo);
    }

    /**
     * 获取当前登录用户（需要带 Authorization: Bearer xxx）
     * userId 由 JwtInterceptor 校验 token 后放入 request attribute
     */
    @GetMapping("/me")
    public Result<User> me(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getById(userId));
    }
}
