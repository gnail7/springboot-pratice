package com.example.springboot.service;

import com.example.springboot.base.PageResult;
import com.example.springboot.controller.AuthController;
import com.example.springboot.dto.login.RegisterDTO;
import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.RoleMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.utils.JwtUtil;
import com.example.springboot.vo.login.LoginUserVO;
import com.example.springboot.vo.user.UserQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 用户服务：登录/注册、用户 CRUD、用户角色分配、当前登录用户信息
 */
@Slf4j
@Service
public class UserService {

    /** 新注册用户默认绑定的角色标识（sql.md 中 role_key='common' 的普通角色） */
    private static final String DEFAULT_ROLE_KEY_COMMON = "common";

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserMapper userMapper,
                       RoleMapper roleMapper,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /* ---------------- 认证 ---------------- */

    @Operation(summary = "登录")
    public AuthController.LoginVO login(AuthController.LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if ("1".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被停用");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUserName());

        AuthController.LoginVO vo = new AuthController.LoginVO();
        vo.setToken(token);
        vo.setUser(user);
        return vo;
    }

    @Operation(summary = "注册")
    public void register(RegisterDTO dto) {
        User exist = userMapper.isUserExist(dto.getUsername(), dto.getPhone());
        if (exist != null) {
            throw new BusinessException(400, "用户名或手机号已注册");
        }

        User user = new User();
        user.setUserName(dto.getUsername());
        // 昵称非空，默认取用户名
        user.setNickName(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus("0");
        userMapper.insert(user);

        // 注册成功后默认绑定“普通角色”（role_key = common，见 sql.md 初始化数据）
        Role commonRole = roleMapper.selectByRoleKey(DEFAULT_ROLE_KEY_COMMON);
        if (commonRole != null) {
            roleMapper.insertUserRole(user.getUserId(), Collections.singletonList(commonRole.getRoleId()));
        }
    }

    /**
     * 根据 token 解析出的 userId，返回当前用户 + 角色 + 权限标识
     */
    @Operation(summary = "当前登录用户信息")
    public LoginUserVO getLoginUser(Long userId) {
        User user = getById(userId);

        LoginUserVO vo = new LoginUserVO();
        vo.setUser(user);
        vo.setRoles(roleMapper.selectRolesByUserId(userId));

        List<String> perms = roleMapper.selectPermsByUserId(userId);
        vo.setPermissions(perms == null ? Collections.emptyList() : perms);
        return vo;
    }

    /* ---------------- 用户 CRUD ---------------- */

    @Operation(summary = "用户分页查询")
    public PageResult<User> page(UserQueryDTO dto) {
        int pn = (dto.getPageNum() == null || dto.getPageNum() < 1) ? 1 : dto.getPageNum();
        int ps = (dto.getPageSize() == null || dto.getPageSize() < 1) ? 10 : dto.getPageSize();
        dto.setPageNum(pn);
        dto.setPageSize(ps);
        dto.setOffset((pn - 1) * ps);

        List<User> records = userMapper.page(dto);
        Long total = userMapper.count(dto);

        return new PageResult<>(records, total, (long) pn, (long) ps);
    }

    public User getById(Long userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    /** 管理端新增用户，返回新用户 id */
    public Long createUser(User user) {
        if (!StringUtils.hasText(user.getUserName())) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(400, "密码不能为空");
        }
        if (userMapper.findByUsername(user.getUserName()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (!StringUtils.hasText(user.getNickName())) {
            user.setNickName(user.getUserName());
        }
        if (!StringUtils.hasText(user.getStatus())) {
            user.setStatus("0");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return user.getUserId();
    }

    /** 修改用户基本信息（不含密码） */
    public void updateUser(User user) {
        if (user.getUserId() == null) {
            throw new BusinessException(400, "缺少用户ID");
        }
        // 用户名改成别人已占用的名字时拦截
        if (StringUtils.hasText(user.getUserName())) {
            User same = userMapper.findByUsername(user.getUserName());
            if (same != null && !same.getUserId().equals(user.getUserId())) {
                throw new BusinessException(400, "用户名已存在");
            }
        }
        if (userMapper.update(user) == 0) {
            throw new BusinessException(404, "用户不存在");
        }
    }

    /** 重置密码 */
    public void resetPassword(Long userId, String newPassword) {
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BusinessException(400, "新密码长度不能少于 6 位");
        }
        if (userMapper.updatePassword(userId, passwordEncoder.encode(newPassword)) == 0) {
            throw new BusinessException(404, "用户不存在");
        }
    }

    /** 删除用户（逻辑删除 + 清理其角色关联） */
    public void deleteUser(Long userId) {
        if (userMapper.deleteByUserId(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }
        roleMapper.deleteUserRoleByUserId(userId);
    }

    /* ---------------- 用户-角色 ---------------- */

    /** 某用户拥有的角色列表 */
    public List<Role> getUserRoles(Long userId) {
        getById(userId);
        return roleMapper.selectRolesByUserId(userId);
    }

    /** 给用户分配角色（覆盖式） */
    public void assignRoles(Long userId, List<Long> roleIds) {
        getById(userId);
        roleMapper.deleteUserRoleByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            roleMapper.insertUserRole(userId, roleIds);
        }
    }
}
