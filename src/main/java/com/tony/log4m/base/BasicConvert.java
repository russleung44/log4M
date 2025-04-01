package com.tony.log4m.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface BasicConvert<SOURCE, TARGET> {

    @Mappings({})
    @InheritConfiguration
    TARGET toTarget(SOURCE source);

    @InheritConfiguration
    List<TARGET> toTargetList(Collection<SOURCE> source);

    @InheritInverseConfiguration
    SOURCE toSource(TARGET source);

    @InheritInverseConfiguration
    List<SOURCE> toSourceList(Collection<TARGET> source);


    @Named("toJson")
    default String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.toJSONString(obj);
    }

    @Named("toList")
    default List toList(String str) {
        if (StrUtil.isBlank(str)) {
            return new ArrayList<>();
        } else {
            return JSON.parseArray(str, Object.class);
        }
    }
}
