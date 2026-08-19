package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {

        User user = userService.getById(id);

        // 用户不存在：抛出业务异常，由 GlobalExceptionHandler 统一转成 Result
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        return user;
    }
}
