package org.ricky.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * 对象校验工具类
 *
 * @author Ricky
 * @version 1.0
 * @date 2024/10/24
 */
public class ValidationUtil {

    // --------------------- 字符串校验相关 ---------------------

    /**
     * 如果字符串是空字符串或仅包含空白字符，则返回 <code>null</code>，否则返回原字符串
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串，可能是原字符串或 <code>null</code>
     */
    public static String nullIfBlank(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        return string;
    }

    /**
     * 检查字符串是否为空或仅包含空白字符
     *
     * @param cs 字符序列，可以是字符串或其他字符类型的集合
     * @return 如果为空或仅空白字符，返回 <code>true</code>
     */
    public static boolean isBlank(CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    /**
     * 检查字符串是否非空且不包含空白字符
     *
     * @param cs 字符序列，可以是字符串或其他字符类型的集合
     * @return 如果非空且不包含空白字符，返回 <code>true</code>
     */
    public static boolean isNotBlank(CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    // --------------------- 集合校验相关 ---------------------

    /**
     * 检查集合是否为空，包括集合为 <code>null</code> 或集合大小为 0
     *
     * @param coll 待检查的集合
     * @return 如果为空，返回 <code>true</code>
     */
    public static boolean isEmpty(Collection<?> coll) {
        return CollectionUtils.isEmpty(coll);
    }

    /**
     * 检查集合是否非空，即集合不为 <code>null</code> 且集合大小大于 0
     *
     * @param coll 待检查的集合
     * @return 如果非空，返回 <code>true</code>
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return CollectionUtils.isNotEmpty(coll);
    }

    // --------------------- Map 校验相关 ---------------------

    /**
     * 检查 Map 是否为空，包括 Map 为 <code>null</code> 或 Map 内容为空
     *
     * @param map 待检查的 Map
     * @return 如果为空，返回 <code>true</code>
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 检查 Map 是否非空，即 Map 不为 <code>null</code> 且 Map 内容不为空
     *
     * @param map 待检查的 Map
     * @return 如果非空，返回 <code>true</code>
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    // --------------------- 对象校验相关 ---------------------

    /**
     * 确保对象非 <code>null</code>
     * 如果对象为 <code>null</code>，则抛出 {@link IllegalArgumentException} 异常
     *
     * @param obj     待检查的对象
     * @param message 异常信息
     * @param <T>     对象类型
     * @return 非空对象
     */
    public static <T> T requireNonNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }

    /**
     * 检查对象是否为 <code>null</code>
     *
     * @param obj 待检查的对象
     * @return 如果对象为 <code>null</code>，返回 <code>true</code>
     */
    public static boolean isNull(Object obj) {
        return Objects.isNull(obj);
    }

    /**
     * 检查对象是否非 <code>null</code>
     *
     * @param obj 待检查的对象
     * @return 如果对象非 <code>null</code>，返回 <code>true</code>
     */
    public static boolean nonNull(Object obj) {
        return Objects.nonNull(obj);
    }

    // --------------------- 对象比较相关 ---------------------

    /**
     * 检查两个对象是否相等
     * 使用 {@link Objects#equals} 方法进行比较
     *
     * @param obj1 第一个对象
     * @param obj2 第二个对象
     * @return 如果两个对象相等，返回 <code>true</code>
     */
    public static boolean equals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    /**
     * 检查两个对象是否不相等
     * 使用 {@link Objects#equals} 方法进行比较，并取反
     *
     * @param obj1 第一个对象
     * @param obj2 第二个对象
     * @return 如果两个对象不相等，返回 <code>true</code>
     */
    public static boolean notEquals(Object obj1, Object obj2) {
        return !equals(obj1, obj2);
    }

    // -------------------- bool 校验相关 --------------------

    public static boolean isTrue(Boolean expr) {
        return TRUE.equals(expr);
    }

    public static boolean isFalse(Boolean expr) {
        return FALSE.equals(expr);
    }

    // --------------------- 异常抛出相关 ---------------------

    /**
     * 抛出异常
     * 如果传入字符串为空或仅包含空白字符，将抛出 {@link IllegalArgumentException} 异常
     *
     * @param str     待检查的字符串
     * @param message 异常信息
     * @return 非空字符串
     */
    public static String requireNotBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    /**
     * 抛出异常
     * 如果传入集合为空，将抛出 {@link IllegalArgumentException} 异常
     *
     * @param coll    待检查的集合
     * @param message 异常信息
     * @param <T>     集合元素类型
     * @return 非空集合
     */
    public static <T> Collection<T> requireNotEmpty(Collection<T> coll, String message) {
        if (isEmpty(coll)) {
            throw new IllegalArgumentException(message);
        }
        return coll;
    }

    /**
     * 抛出异常
     * 如果传入 Map 为空，将抛出 {@link IllegalArgumentException} 异常
     *
     * @param map     待检查的 Map
     * @param message 异常信息
     * @param <K>     Map 的键类型
     * @param <V>     Map 的值类型
     * @return 非空 Map
     */
    public static <K, V> Map<K, V> requireNotEmpty(Map<K, V> map, String message) {
        if (isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    public static void requireTrue(boolean expr, String message) {
        if(!expr) {
            throw new IllegalArgumentException(message);
        }
    }
}