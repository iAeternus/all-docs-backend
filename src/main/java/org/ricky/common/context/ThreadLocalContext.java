package org.ricky.common.context;

import static org.ricky.common.util.ValidationUtil.isNull;

/**
 * 线程上下文管理类，通过 {@link ThreadLocal} 提供线程隔离的上下文存储。
 * <p>
 * 用于存储和管理用户上下文信息，确保每个线程独立访问和修改上下文。
 * </p>
 *
 * <p>
 * <b>注意事项：</b>
 * <ul>
 *     <li><b>线程池：</b> 线程复用可能导致上下文信息残留，需在任务执行前后清理。</li>
 *     <li><b>异步任务：</b> 上下文信息不会自动传递到子线程，需手动处理。</li>
 *     <li><b>内存泄漏：</b> 上下文信息不再需要时，必须调用 {@link #removeContext()}。</li>
 * </ul>
 * </p>
 *
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 */
public class ThreadLocalContext {

    /**
     * 线程局部变量，存储当前线程的用户上下文信息。
     */
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程的用户上下文信息。
     *
     * @param userContext 用户上下文信息，不能为空。
     * @throws IllegalArgumentException 如果传入的用户上下文为 <code>null</code>。
     */
    public static void setContext(UserContext userContext) {
        if (isNull(userContext)) {
            throw new IllegalArgumentException("UserContext cannot be null");
        }
        CONTEXT.set(userContext);
    }

    /**
     * 获取当前线程的用户上下文信息。
     *
     * @return 当前线程的用户上下文信息，可能为 <code>null</code>。
     */
    public static UserContext getContext() {
        return CONTEXT.get();
    }

    /**
     * 清除当前线程的用户上下文信息，避免内存泄漏。
     * <p>
     * <b>注意：</b> 在线程池或异步任务中，必须在任务结束时调用此方法。
     * </p>
     */
    public static void removeContext() {
        CONTEXT.remove();
    }
}