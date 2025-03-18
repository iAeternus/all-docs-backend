package org.ricky.core.common.converter.type;

import com.google.common.collect.Maps;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Map;

import static org.ricky.core.common.util.ValidationUtil.isNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className IntegerCodeToEnumConverterFactory
 * @desc
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IntegerCodeToEnumConverterFactory implements ConverterFactory<Integer, BaseEnum> {

    private static final Map<Class, Converter> CONVERTERS = Maps.newHashMap();

    /**
     * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型
     *
     * @param targetType 转换后的类型
     * @return 返回一个转化器
     */
    @NonNull
    @Override
    public <T extends BaseEnum> Converter<Integer, T> getConverter(@NonNull Class<T> targetType) {
        Converter converter = CONVERTERS.get(targetType);
        if (isNull(converter)) {
            converter = new StringToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
