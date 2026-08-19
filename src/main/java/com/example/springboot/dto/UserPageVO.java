package com.example.springboot.dto;

import com.example.springboot.vo.UserVO;
import lombok.Data;

import java.util.List;

@Data
public class UserPageVO {

    private List<UserVO> records;

    private Long total;

    private Integer current;

    private Integer size;
}