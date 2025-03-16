package org.ricky.core.common.converter.type;

import com.google.common.collect.Maps;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

import static org.ricky.common.util.ValidationUtil.isNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className StringToEnumConverter
 * @desc 字符串转枚举
 */
public class StringToEnumConverter<T extends BaseEnum> implements Converter<String, T> {

    private final Map<String, T> enumMap = Maps.newHashMap();

    public StringToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getCode().toString(), e);
        }
    }

    @Override
    public T convert(@NonNull String source) {
        T t = enumMap.get(source);
        if (isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
