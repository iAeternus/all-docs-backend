package org.ricky.common.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/11/14
 * @className LockedMethod
 * @desc
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LockedMethod {

    String key() default "";

    long timeout() default 0;

    TimeUnit unit() default TimeUnit.SECONDS;

}
