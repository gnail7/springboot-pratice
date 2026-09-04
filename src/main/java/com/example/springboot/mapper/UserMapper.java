package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import com.example.springboot.vo.user.UserQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper（XML 实现，表 sys_user）
 */
@Mapper
public interface UserMapper {

    /** 按登录账号查询（登录用） */
    User findByUsername(@Param("userName") String userName);

    /** 判断用户名或手机号是否已存在（注册用） */
    User isUserExist(@Param("userName") String userName, @Param("phone") String phone);

    /** 按主键查用户 */
    User selectByUserId(@Param("userId") Long userId);

    /** 条件分页 */
    List<User> page(UserQueryDTO dto);

    /** 条件计数 */
    Long count(UserQueryDTO dto);

    /**
     * 新增（返回自增主键到 user.userId）
     */
    void insert(User user);

    /** 修改基本信息（不含密码） */
    int update(User user);

    /** 修改密码 */
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    /** 逻辑删除（del_flag = 1） */
    int deleteByUserId(@Param("userId") Long userId);
}
