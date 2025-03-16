package org.ricky.common.json;

import java.lang.annotation.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/1/2
 * @className JsonTypeDefine
 * @desc json子类型扩展
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JsonTypeDefine {

    String value() default "";

    String desc() default "";

}
