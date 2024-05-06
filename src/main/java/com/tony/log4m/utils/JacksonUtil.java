package com.tony.log4m.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tony.log4m.config.JacksonConfig;
import com.tony.log4m.exception.Log4mException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * jackson工具类
 *
 * @author TonyLeung
 * @since 2023/4/10
 */
@Slf4j
public class JacksonUtil {

    private static final JsonMapper objectMapper = JacksonConfig.getJsonMapper();

    public static String toStr(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("jackson toStr error", e);
        }
        return null;
    }

    public static <T> List<T> toList(String body, Class<T> clazz) {
        if (StrUtil.isBlank(body)) {
            return null;
        }
        CollectionType collectionType = TypeFactory.defaultInstance()
                .constructCollectionType(ArrayList.class, clazz);
        try {
            return objectMapper.readValue(body, collectionType);
        } catch (Exception e) {
            log.error("jackson toList error", e);
        }
        return null;
    }

    public static <T> T toObj(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            log.error("jackson toObj error", e);
            throw new Log4mException("jackson toObj error");
        }
    }

    public static <T> T toObj(String body, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(body, valueTypeRef);
        } catch (JsonProcessingException e) {
            log.error("jackson toObj error", e);
            throw new Log4mException("jackson toObj error");
        }
    }


    public static Map<String, Object> toMap(Object obj) {
        try {
            return toMap(objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            log.error("jackson toMap error", e);
        }
        return null;
    }

    public static Map<String, Object> toMap(String body) {
        MapType mapType = TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class);
        try {
            return objectMapper.readValue(body, mapType);
        } catch (JsonProcessingException e) {
            log.error("jackson反序列化失败", e);
        }
        return null;
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

}
