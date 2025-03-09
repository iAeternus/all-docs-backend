package org.ricky.core.common.validation.collection;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/24
 * @className NoDuplicatedStringValidator
 * @desc
 */
public class NoDuplicatedStringValidator implements ConstraintValidator<NoDuplicatedString, Collection<String>> {
    @Override
    public boolean isValid(Collection<String> collection, ConstraintValidatorContext constraintValidatorContext) {
        if (CollectionUtils.isEmpty(collection)) {
            return true;
        }

        return distinct(collection).size() == collection.size();
    }

    private List<String> distinct(Collection<String> collection) {
        return collection.stream()
                .distinct()
                .collect(toImmutableList());
    }
}
