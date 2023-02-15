package com.tony.log4m.base;

import com.github.pagehelper.IPage;
import lombok.Data;

@Data
public class PageDTO<T> implements IPage {
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;
    private T param;
}
