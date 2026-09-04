package com.example.springboot.vo.login;

import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息：用户 + 角色 + 菜单权限标识
 */
@Data
public class LoginUserVO {

    private User user;

    /** 用户拥有的角色 */
    private List<Role> roles;

    /** 角色对应的菜单权限标识（sys_menu.perms，去重后） */
    private List<String> permissions;
}
