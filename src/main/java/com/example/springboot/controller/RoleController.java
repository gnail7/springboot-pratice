package com.example.springboot.controller;

import com.example.springboot.base.PageResult;
import com.example.springboot.entity.Role;
import com.example.springboot.service.RoleService;
import com.example.springboot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理接口：角色 CRUD + 菜单权限(sys_role_menu) + 数据权限部门(sys_role_dept)
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public PageResult<Role> page(@RequestParam(required = false) String roleName,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return roleService.page(roleName, status, pageNum, pageSize);
    }

    @Operation(summary = "全部启用角色（下拉框）")
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{roleId}")
    public Result<Role> get(@PathVariable Long roleId) {
        return Result.success(roleService.getById(roleId));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Long> create(@RequestBody Role role) {
        return Result.success(roleService.create(role));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{roleId}")
    public Result<Void> update(@PathVariable Long roleId, @RequestBody Role role) {
        role.setRoleId(roleId);
        roleService.update(role);
        return Result.success();
    }

    @Operation(summary = "删除角色（逻辑删除，并清理用户/菜单/部门关联）")
    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        roleService.delete(roleId);
        return Result.success();
    }

    /* ---------- 角色-菜单权限 sys_role_menu ---------- */

    @Operation(summary = "查询角色已分配的菜单 id")
    @GetMapping("/{roleId}/menuIds")
    public Result<List<Long>> menuIds(@PathVariable Long roleId) {
        return Result.success(roleService.getMenuIds(roleId));
    }

    @Operation(summary = "保存角色的菜单权限（覆盖式）")
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId,
                                    @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }

    /* ---------- 角色-部门数据权限 sys_role_dept ---------- */

    @Operation(summary = "查询角色已分配的部门 id")
    @GetMapping("/{roleId}/deptIds")
    public Result<List<Long>> deptIds(@PathVariable Long roleId) {
        return Result.success(roleService.getDeptIds(roleId));
    }

    @Operation(summary = "保存角色的数据权限部门（覆盖式）")
    @PutMapping("/{roleId}/depts")
    public Result<Void> assignDepts(@PathVariable Long roleId,
                                    @RequestBody List<Long> deptIds) {
        roleService.assignDepts(roleId, deptIds);
        return Result.success();
    }
}
