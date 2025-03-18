package org.ricky.common.password;

import org.ricky.core.common.util.ValidationUtil;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/11
 * @className IPasswordEncoder
 * @desc 密码编码器
 */
public interface IPasswordEncoder {

    /**
     * 编码
     *
     * @param rawPassword 原始密码
     * @return 编码后的密码
     */
    String encode(CharSequence rawPassword);

    /**
     * 判断原始密码与编码后的密码是否匹配
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 编码后的密码
     * @return true=匹配 false=不匹配
     */
    default boolean matches(CharSequence rawPassword, String encodedPassword) {
        return ValidationUtil.equals(encode(rawPassword), encodedPassword);
    }

}
