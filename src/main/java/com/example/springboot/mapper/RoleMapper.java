package com.example.springboot.mapper;

import com.example.springboot.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper（XML 实现，表 sys_role / sys_user_role / sys_role_menu / sys_role_dept）
 */
@Mapper
public interface RoleMapper {

    /* ---------------- 角色 CRUD ---------------- */

    /** 条件分页 */
    List<Role> page(@Param("roleName") String roleName,
                    @Param("status") String status,
                    @Param("offset") Integer offset,
                    @Param("pageSize") Integer pageSize);

    /** 条件计数 */
    Long count(@Param("roleName") String roleName, @Param("status") String status);

    /** 全部启用角色（下拉用） */
    List<Role> listAll();

    /** 按主键查角色 */
    Role selectByRoleId(@Param("roleId") Long roleId);

    /** 按 role_key 查角色（注册时绑定普通角色用） */
    Role selectByRoleKey(@Param("roleKey") String roleKey);

    int insert(Role role);

    int update(Role role);

    /** 逻辑删除（del_flag = 1） */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /* ---------------- 用户-角色（sys_user_role） ---------------- */

    /** 删除该角色下的所有用户关联 */
    int deleteUserRoleByRoleId(@Param("roleId") Long roleId);

    /** 某用户的角色 id 列表（回显用） */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /** 某用户的角色列表（/me 用） */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /** 删除某用户的全部角色关联 */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /** 给用户批量绑定角色 */
    int insertUserRole(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /* ---------------- 角色-菜单（sys_role_menu） ---------------- */

    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    int deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /* ---------------- 角色-部门（sys_role_dept） ---------------- */

    List<Long> selectDeptIdsByRoleId(@Param("roleId") Long roleId);

    int deleteRoleDeptByRoleId(@Param("roleId") Long roleId);

    int insertRoleDept(@Param("roleId") Long roleId, @Param("deptIds") List<Long> deptIds);

    /* ---------------- 权限标识（用户→角色→菜单） ---------------- */

    /** 某用户的全部菜单权限标识 perms（去重），供接口鉴权使用 */
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
