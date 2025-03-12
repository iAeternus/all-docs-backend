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

    String MOBILE_PATTERN = "^[1]([3-9])[0-9]{9}$";
    String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    String PASSWORD_PATTERN = "^[A-Za-z\\d!@#$%^&*()_+]{6,32}$";
    String VERIFICATION_CODE_PATTERN = "^[0-9]{6}$";
    String DATE_PATTERN = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";
    String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    String CONTROL_ALIAS_PATTERN = "^[A-Za-z]{1,10}$";
    String UNIFIED_CODE_PATTERN = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    String BANK_ACCOUNT_PATTERN = "^[1-9]\\d{9,29}$";
    String PHONE_PATTERN = "^[\\d\\s\\-]{5,15}$";
    String RGBA_COLOR_PATTERN = "^rgba\\(\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*,\\s*((0.[0-9]{0,2})|[01]|(.[0-9]{1,2}))\\s*\\)$";
    String HEX_COLOR_PATTERN = "^#[0-9a-f]{3}([0-9a-f]{3})?$";
    String RGB_COLOR_PATTERN = "^rgb\\(\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])%?\\s*\\)$";
    String SUBDOMAIN_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9-]{0,20}[a-zA-Z0-9]$";
    String IP_PATTERN = "^(?:localhost|(?:(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(?:\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))$";

}
