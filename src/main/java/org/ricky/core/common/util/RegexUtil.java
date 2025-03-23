package org.ricky.core.common.util;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className RegexUtil
 * @desc
 */
public class RegexUtil {

    public static Pattern fuzzySearchKeyword(String keyword) {
        return compile("^.*" + keyword + ".*$", CASE_INSENSITIVE);
    }

}
