package com.example.springboot.base;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> records;

    private Long total;

    private Long pageNum;

    private Long pageSize;

    public PageResult(List<T> records, Long total, Long pageNum, Long pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}