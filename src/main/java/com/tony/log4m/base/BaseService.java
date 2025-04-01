package com.tony.log4m.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 基础Service
 */
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseEntity<T>> extends ServiceImpl<M, T> {


}
