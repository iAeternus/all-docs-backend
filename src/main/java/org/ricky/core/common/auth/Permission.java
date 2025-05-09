package org.ricky.core.common.auth;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className Permission
 * @desc 设置访问权限，可根据不同接口要求，设置为管理员或者普通用户
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Permission {

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("value")
    PermissionEnum[] name() default {};

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("name")
    PermissionEnum[] value() default {};

}
