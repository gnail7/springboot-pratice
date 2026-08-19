package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> selectUsers(
            @Param("name") String name,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );

    Long countUsers(
            @Param("name") String name
    );
}