package com.tony.log4m.base;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.List;

public interface CrudService<T extends BaseEntity, E> extends IService<T> {

    PageInfo<E> page(Integer pageNum, Integer pageSize);

    E get(Serializable id);

    E insert(T t);

    E insert(E e);

    E update(T t);

    E update(E e);

    void delete(Serializable id);

    boolean deleteAll(String[] ids);


    E toTarget(T t);

    List<E> toTargetList(List<T> entitys);

    T toSource(E e);

    List<T> toSourceList(List<E> models);

    boolean checkName(String name, Serializable id);
}





