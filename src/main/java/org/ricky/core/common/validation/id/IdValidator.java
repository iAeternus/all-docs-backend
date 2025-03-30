package org.ricky.core.common.validation.id;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

import static org.ricky.core.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/12/28
 * @className IdValidator
 * @desc
 */
public class IdValidator implements ConstraintValidator<Id, String> {

    static final String DEFAULT_MESSAGE = "ID format is incorrect.";

    private String[] prefixes;
    private String message;

    @Override
    public void initialize(Id id) {
        this.prefixes = id.pre();
        this.message = id.message();
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        if (isBlank(id)) {
            return true;
        }

        boolean isValid = isId(id, prefixes);
        if (!isValid) {
            String customMessage = isBlank(message) ? DEFAULT_MESSAGE : message;
            String finalMessage = Arrays.toString(prefixes) + " " + customMessage;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(finalMessage).addConstraintViolation();
        }

        return isValid;
    }

    public static boolean isId(String id, String[] prefixes) {
        return PrefixFactory.getInstance().matches(id, prefixes);
    }

    public static boolean isId(String id, String prefix) {
        return PrefixFactory.getInstance().matches(id, new String[]{prefix});
    }

}
