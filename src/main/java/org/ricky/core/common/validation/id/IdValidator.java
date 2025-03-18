package org.ricky.core.common.validation.id;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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

    private String prefix;
    private String message;

    @Override
    public void initialize(Id id) {
        this.prefix = id.pre();
        this.message = id.message();
    }

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        if (isBlank(id)) {
            return true;
        }

        boolean isValid = isId(id, prefix);
        if (!isValid) {
            String customMessage = isBlank(message) ? DEFAULT_MESSAGE : message;
            String finalMessage = prefix + " " + customMessage;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(finalMessage).addConstraintViolation();
        }

        return isValid;
    }

    public static boolean isId(String id, String prefix) {
        return PrefixFactory.getInstance().matches(id, prefix);
    }

}
