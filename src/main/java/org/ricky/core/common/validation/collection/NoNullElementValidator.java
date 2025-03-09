package org.ricky.core.common.validation.collection;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/9
 * @className NoNullElementValidator
 * @desc 列表没有空元素校验器
 */
@SuppressWarnings("rawtypes")
public class NoNullElementValidator implements ConstraintValidator<NoNullElement, Collection> {

    @Override
    public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
        if (isEmpty(collection)) {
            return true;
        }

        return !collection.contains(null);
    }

}
