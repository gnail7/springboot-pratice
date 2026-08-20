package com.example.springboot.vo;

import com.example.springboot.entity.User;
import lombok.Data;

/**
 * 登录成功返回结果
 */
@Data
public class LoginVO {

    /** JWT token */
    private String token;

    /** 用户信息（password 字段已被 @JsonIgnore 忽略，不会返回） */
    private User user;
}
