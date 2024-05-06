package com.tony.log4m.base;

import lombok.Data;

@Data
public class PageDTO<T> {
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;
    private T param;
}
