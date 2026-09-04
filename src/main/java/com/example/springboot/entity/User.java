package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表 sys_user（列名为 user_id / user_name / nick_name ...，
 * 由 MyBatis-Plus 默认开启的下划线转驼峰自动映射到本类字段）
 */
@Data
@TableName("sys_user")
public class User {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private Long deptId;

    /** 登录账号 */
    private String userName;

    /** 昵称 */
    private String nickName;

    /** 手机号 */
    private String phone;

    private String email;

    private String sex;

    private String avatar;

    /** 密码（BCrypt 加密存储；序列化时忽略，避免泄漏） */
    @JsonIgnore
    private String password;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0存在 1删除） */
    private String delFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
