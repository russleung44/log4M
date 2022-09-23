package com.tony.log4m.base;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Slf4j
public abstract class CrudServiceImpl<M extends BaseMapper<T>, T extends BaseEntity, E, C extends BasicConvert> extends BaseService<M, T> implements CrudService<T, E> {

    @Autowired
    private C convert;

    @Override
    @Transactional(readOnly = true)
    public E get(Serializable id) {
        return toTarget(Optional.ofNullable(super.getById(id)).orElseThrow());
    }


    @Override
    public E insert(T model) {
        if (model == null) {
            return null;
        }

        // 将主键设为 null
        boolean ret = model.setId(null).insert();
        if (ret) {
            return toTarget(model);
        }
        return null;
    }

    @Override
    public E insert(E v) {
        T t = toSource(v);
        return insert(t);
    }


    @Override
    public E update(T model) {
        if (model == null) {
            return null;
        }

        boolean ret = super.updateById(model);
        if (ret) {
            return toTarget(model);
        } else {
            return null;
        }
    }

    @Override
    public void delete(Serializable id) {
        Optional.ofNullable(super.getById(id)).orElseThrow().deleteById();
    }


    @Override
    @Transactional
    public boolean deleteAll(String[] ids) {
        if (ids == null) {
            return false;
        }
        List<String> idList = Convert.toList(String.class, ids);
        return super.removeByIds(idList);
    }


    @Override
    public E toTarget(T t) {
        return (E) convert.toTarget(t);
    }

    @Override
    public List<E> toTargetList(List<T> entitys) {
        return convert.toTargetList(entitys);
    }

    @Override
    public T toSource(E v) {
        return (T) convert.toSource(v);
    }

    @Override
    public List<T> toSourceList(List<E> models) {
        return convert.toSourceList(models);
    }
}
