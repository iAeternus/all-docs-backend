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
 * @className IntegerToEnumConverter
 * @desc 整数转枚举
 */
public class IntegerToEnumConverter<T extends BaseEnum> implements Converter<Integer, T> {

    private final Map<Integer, T> enumMap = Maps.newHashMap();

    public IntegerToEnumConverter(Class<T> enumType) {
        T[] enums = enumType.getEnumConstants();
        for (T e : enums) {
            enumMap.put(e.getCode(), e);
        }
    }

    @Override
    public T convert(@NonNull Integer source) {
        T t = enumMap.get(source);
        if (isNull(t)) {
            throw new IllegalArgumentException("无法匹配对应的枚举类型");
        }
        return t;
    }
}
