package com.example.springboot.controller;

import com.example.springboot.base.PageResult;
import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.Result;
import com.example.springboot.vo.user.UserQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理接口：CRUD + 角色分配
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public PageResult<User> page(UserQueryDTO dto) {
        return userService.page(dto);
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{userId}")
    public Result<User> get(@PathVariable Long userId) {
        return Result.success(userService.getById(userId));
    }

    @Operation(summary = "查询用户已分配的角色")
    @GetMapping("/{userId}/roles")
    public Result<List<Role>> roles(@PathVariable Long userId) {
        return Result.success(userService.getUserRoles(userId));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Long> create(@RequestBody User user) {
        return Result.success(userService.createUser(user));
    }

    @Operation(summary = "修改用户（不含密码）")
    @PutMapping("/{userId}")
    public Result<Void> update(@PathVariable Long userId, @RequestBody User user) {
        user.setUserId(userId);
        userService.updateUser(user);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{userId}/password")
    public Result<Void> resetPassword(@PathVariable Long userId,
                                      @RequestBody @Valid PasswordDTO dto) {
        userService.resetPassword(userId, dto.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "给用户分配角色（覆盖式）")
    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable Long userId,
                                    @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.success();
    }

    @Operation(summary = "删除用户（逻辑删除）")
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    /** 重置密码请求参数 */
    @Data
    public static class PasswordDTO {

        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}
