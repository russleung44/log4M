package com.tony.log4m.base;


import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

public interface CrudService<T extends BaseEntity, E> extends IService<T> {

    E get(Serializable id);

    E insert(T t);

    E insert(E e);

    E update(T t);

    void delete(Serializable id);

    boolean deleteAll(String[] ids);


    E toTarget(T t);

    List<E> toTargetList(List<T> entitys);

    T toSource(E e);

    List<T> toSourceList(List<E> models);
}





