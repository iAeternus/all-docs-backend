package org.ricky.core.common.validation.collection;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/24
 * @className NoBlankStringValidator
 * @desc
 */
public class NoBlankStringValidator implements ConstraintValidator<NoBlankString, Collection<String>> {
    @Override
    public boolean isValid(Collection<String> collection, ConstraintValidatorContext constraintValidatorContext) {
        if (CollectionUtils.isEmpty(collection)) {
            return true;
        }

        return collection.stream()
                .noneMatch(StringUtils::isBlank);
    }
}
