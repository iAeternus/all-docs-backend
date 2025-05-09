package org.ricky.core.common.validation.collection;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/24
 * @className NoDuplicatedString
 * @desc
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicatedStringValidator.class)
@Documented
public @interface NoDuplicatedString {

    String message() default "String must not be duplicated.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
