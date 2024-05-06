package com.tony.log4m.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author TonyLeung
 * @since 2021/9/18
 */
@Configuration
public class JacksonConfig {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    public static JsonMapper getJsonMapper() {
        return jsonMapper();
    }

    @Bean
    public static JsonMapper jsonMapper() {
        JsonMapper jsonMapper = JsonMapper.builder()
                // enum
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                // 禁用遇到未知属性抛出异常
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 禁用序列化日期为timestamps, 禁用空bean转json错误
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();

        // 设置序列化时，日期的统一格式
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 注册JavaTimeModule模块
        jsonMapper.registerModule(javaTimeModule());

        // 设置NullSerializer
        jsonMapper.setSerializerFactory(jsonMapper.getSerializerFactory().withSerializerModifier(beanSerializerModifier()));

        // long -> String
        jsonMapper.registerModule(longToStringModule());

        return jsonMapper;
    }


    @Bean
    public static SimpleModule longToStringModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);

        return simpleModule;
    }


    @Bean
    public static JavaTimeModule javaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime序列化与反序列化
        javaTimeModule.addSerializer(new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        // LocalDate序列化与反序列化
        javaTimeModule.addSerializer(new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        // LocalTime序列化与反序列化
        javaTimeModule.addSerializer(new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        return javaTimeModule;
    }

    @Bean
    public static BeanSerializerModifier beanSerializerModifier() {
        return new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
                for (var beanPropertyWriter : beanProperties) {
                    var javaType = beanPropertyWriter.getType();
                    if (javaType.isArrayType() || javaType.isCollectionLikeType()) {
                        beanPropertyWriter.assignNullSerializer(arrayOrCollectionNullSerializer());
                    } else if (javaType.isTypeOrSubTypeOf(String.class)) {
                        beanPropertyWriter.assignNullSerializer(stringNullSerializer());
                    } else if (javaType.isTypeOrSuperTypeOf(Boolean.class)) {
                        beanPropertyWriter.assignNullSerializer(booleanNullSerializer());
                    } else if (javaType.isMapLikeType()) {
                        beanPropertyWriter.assignNullSerializer(mapNullSerializer());
                    } else if (javaType.isTypeOrSuperTypeOf(Integer.class) ||
                            javaType.isTypeOrSuperTypeOf(Long.class) ||
                            javaType.isTypeOrSuperTypeOf(Double.class) ||
                            javaType.isTypeOrSuperTypeOf(BigDecimal.class) ||
                            javaType.isTypeOrSuperTypeOf(Float.class)) {
                        beanPropertyWriter.assignNullSerializer(numberNullSerializer());
                    } else if (javaType.isTypeOrSuperTypeOf(LocalDateTime.class) ||
                            javaType.isTypeOrSuperTypeOf(LocalDate.class)) {
                        beanPropertyWriter.assignNullSerializer(dateNullSerializer());
                    }
                }

                return beanProperties;
            }
        };
    }


    @Bean
    public static JsonSerializer<Object> arrayOrCollectionNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object o, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
                gen.writeObject(new ArrayList<>());
            }
        };
    }

    @Bean
    public static JsonSerializer<Object> stringNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("");
            }
        };
    }

    @Bean
    public static JsonSerializer<Object> booleanNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeBoolean(false);
            }
        };
    }

    @Bean
    public static JsonSerializer<Object> mapNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeObject(new HashMap());
            }
        };
    }

    @Bean
    public static JsonSerializer<Object> numberNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeNumber(0);
            }
        };
    }

    @Bean
    public static JsonSerializer<Object> dateNullSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("");
            }
        };
    }
}
