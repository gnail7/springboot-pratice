package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@TableName("user")
public class User {
    @TableId
    private Long id;

    private String username;

    /** 密码（BCrypt 加密存储；序列化时忽略，避免泄漏） */
    @JsonIgnore
    private String password;

    private Integer age;
}