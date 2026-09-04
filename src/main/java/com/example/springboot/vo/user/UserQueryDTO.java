package com.example.springboot.vo.user;

import lombok.Data;

/**
 * 用户条件分页查询
 */
@Data
public class UserQueryDTO {

    private String userName;

    private String nickName;

    private String phone;

    private String status;

    private String sex;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 由 pageNum/pageSize 计算得出，查询前由 service 填充（XML 里 LIMIT #{offset}, #{pageSize}） */
    private Integer offset;
}
