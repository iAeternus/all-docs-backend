package org.ricky.common.constants;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className RegexConstant
 * @desc 正则表达式<br>
 * 规范：<br>
 * 1. 常量命名必须以`_REG`结尾<br>
 */
public interface RegexConstant {

    /**
     * 中英文下划线横向，1-64位
     */
    String CH_ENG_WORD_REG = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{1,64}$";

    /**
     * 只能由数字，大小字母，下划线组成
     */
    String NUM_WORD_REG = "^[A-Za-z0-9_]+$";

}
