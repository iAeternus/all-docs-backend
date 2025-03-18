package org.ricky.core.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.time.ZoneId.of;
import static java.util.TimeZone.getTimeZone;
import static org.ricky.common.constants.ConfigConstant.CHINA_TIME_ZONE;
import static org.ricky.core.common.util.ValidationUtil.nullIfBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className MyObjectMapper
 * @desc 自定义对象转换器
 */
public class MyObjectMapper extends ObjectMapper {

    public MyObjectMapper() {
        configure(this);
    }

    /**
     * 配置ObjectMapper
     *
     * @param objectMapper ObjectMapper实例
     */
    private static void configure(ObjectMapper objectMapper) {
        objectMapper.findAndRegisterModules()
                .setTimeZone(getTimeZone(of(CHINA_TIME_ZONE)))
                .setVisibility(ALL, NONE)
                .setVisibility(FIELD, ANY)
                .registerModule(instantModule())
                .registerModule(trimStringModule())
                .configure(WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 序列化和反序列化Instant类型
     */
    private static SimpleModule instantModule() {
        return new SimpleModule()
                .addSerializer(Instant.class, instantSerializer())
                .addDeserializer(Instant.class, instantDeserializer());
    }

    /**
     * 在反序列化时修剪字符串
     */
    private static SimpleModule trimStringModule() {
        return new SimpleModule()
                .addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
                    @Override
                    public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                        return nullIfBlank(jsonParser.getValueAsString().trim());
                    }
                });
    }

    private static JsonDeserializer<Instant> instantDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public Instant deserialize(JsonParser p, DeserializationContext d) throws IOException {
                return Instant.ofEpochMilli(p.getValueAsLong());
            }
        };
    }

    private static JsonSerializer<Instant> instantSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeNumber(value.toEpochMilli());
            }
        };
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param value 要序列化的对象
     * @return 序列化后的JSON字符串
     * @throws RuntimeException 如果序列化过程中发生异常
     */
    @Override
    public String writeValueAsString(Object value) {
        try {
            return super.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象写入指定的Writer中，以JSON格式
     *
     * @param w     写入的目标Writer
     * @param value 要写入的对象
     * @throws RuntimeException 如果写入过程中发生异常
     */
    @Override
    public void writeValue(Writer w, Object value) {
        try {
            super.writeValue(_jsonFactory.createGenerator(w), value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return super.readValue(content, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return super.readValue(content, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(InputStream src, Class<T> valueType) {
        try {
            return super.readValue(src, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode readTree(String content) {
        try {
            return super.readTree(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
