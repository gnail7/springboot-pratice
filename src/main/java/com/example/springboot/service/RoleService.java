package com.example.springboot.service;

import com.example.springboot.base.PageResult;
import com.example.springboot.entity.Role;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.RoleMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务：角色 CRUD + 用户-角色(sys_user_role) + 角色-菜单(sys_role_menu)
 * + 角色-部门数据权限(sys_role_dept)
 */
@Service
public class RoleService {

    private final RoleMapper roleMapper;

    public RoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Operation(summary = "角色分页查询")
    public PageResult<Role> page(String roleName, String status, Integer pageNum, Integer pageSize) {
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        List<Role> records = roleMapper.page(roleName, status, (pn - 1) * ps, ps);
        Long total = roleMapper.count(roleName, status);

        return new PageResult<>(records, total, (long) pn, (long) ps);
    }

    /** 全部启用角色（下拉框） */
    public List<Role> listAll() {
        return roleMapper.listAll();
    }

    public Role getById(Long roleId) {
        Role role = roleMapper.selectByRoleId(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return role;
    }

    public Long create(Role role) {
        checkRoleKeyUnique(role.getRoleKey(), null);
        if (!StringUtils.hasText(role.getRoleName())) {
            throw new BusinessException(400, "角色名称不能为空");
        }
        fillDefault(role);
        roleMapper.insert(role);
        return role.getRoleId();
    }

    public void update(Role role) {
        if (role.getRoleId() == null) {
            throw new BusinessException(400, "缺少角色ID");
        }
        getById(role.getRoleId());
        checkRoleKeyUnique(role.getRoleKey(), role.getRoleId());
        if (!StringUtils.hasText(role.getRoleName())) {
            throw new BusinessException(400, "角色名称不能为空");
        }
        roleMapper.update(role);
    }

    /** 删除角色（逻辑删除 + 清理关联表） */
    public void delete(Long roleId) {
        getById(roleId);
        roleMapper.deleteByRoleId(roleId);
        roleMapper.deleteUserRoleByRoleId(roleId);
        roleMapper.deleteRoleMenuByRoleId(roleId);
        roleMapper.deleteRoleDeptByRoleId(roleId);
    }

    /* ---------- 角色-菜单权限 sys_role_menu ---------- */

    /** 角色已分配的菜单 id（回显用） */
    public List<Long> getMenuIds(Long roleId) {
        getById(roleId);
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    /** 保存角色的菜单权限（覆盖式：先清空再批量插入） */
    public void assignMenus(Long roleId, List<Long> menuIds) {
        getById(roleId);
        roleMapper.deleteRoleMenuByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMapper.insertRoleMenu(roleId, menuIds);
        }
    }

    /* ---------- 角色-部门数据权限 sys_role_dept ---------- */

    /** 角色已分配的部门 id（回显用） */
    public List<Long> getDeptIds(Long roleId) {
        getById(roleId);
        return roleMapper.selectDeptIdsByRoleId(roleId);
    }

    /** 保存角色的数据权限部门（覆盖式） */
    public void assignDepts(Long roleId, List<Long> deptIds) {
        getById(roleId);
        roleMapper.deleteRoleDeptByRoleId(roleId);
        if (deptIds != null && !deptIds.isEmpty()) {
            roleMapper.insertRoleDept(roleId, deptIds);
        }
    }

    private void fillDefault(Role role) {
        if (role.getRoleSort() == null) {
            role.setRoleSort(0);
        }
        if (!StringUtils.hasText(role.getDataScope())) {
            // 数据范围：1全部 2自定义 3本部门 4本部门及以下 5仅本人
            role.setDataScope("1");
        }
        if (!StringUtils.hasText(role.getStatus())) {
            role.setStatus("0");
        }
    }

    private void checkRoleKeyUnique(String roleKey, Long excludeRoleId) {
        if (!StringUtils.hasText(roleKey)) {
            throw new BusinessException(400, "角色标识(roleKey)不能为空");
        }
        for (Role r : roleMapper.listAll()) {
            if (roleKey.equals(r.getRoleKey())
                    && (excludeRoleId == null || !excludeRoleId.equals(r.getRoleId()))) {
                throw new BusinessException(400, "角色标识已存在：" + roleKey);
            }
        }
    }
}
