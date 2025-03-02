package org.ricky.common.converter.type;

import com.google.common.collect.Maps;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className StringCodeToEnumConverterFactory
 * @desc
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StringCodeToEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    private static final Map<Class, Converter> CONVERTERS = Maps.newHashMap();

    /**
     * 获取一个从 String 转化为 T 的转换器，T 是一个泛型
     *
     * @param targetType 转换后的类型
     * @return 返回一个转化器
     */
    @NonNull
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return CONVERTERS.computeIfAbsent(targetType, clazz -> new StringToEnumConverter<>(targetType));
    }
}
