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
 * @className NoBlankString
 * @desc 集合中不存在空白字符串
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoBlankStringValidator.class)
@Documented
public @interface NoBlankString {

    String message() default "Collection must not contain blank string.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
