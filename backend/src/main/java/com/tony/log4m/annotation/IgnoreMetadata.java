package com.tony.log4m.annotation;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * MapStruct 忽略元数据注解
 *
 * @author Tony
 * @since 11/27/2024
 */
@Retention(RetentionPolicy.CLASS)
@Mapping(target = "crTime", ignore = true)
@Mapping(target = "mdTime", ignore = true)
@Mapping(target = "deleted", ignore = true)
public @interface IgnoreMetadata {
}
