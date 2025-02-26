package org.ricky.common.tracing;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.ricky.common.util.ValidationUtil.nonNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className TracingService
 * @desc 链路追踪
 */
@Component
@RequiredArgsConstructor
public class TracingService {

    private final Tracer tracer;

    /**
     * 获取当前线程的追踪ID。
     * 如果当前线程有活跃的Span，则返回该Span的追踪ID；如果没有，则返回null。
     * 追踪ID是全局唯一的，用于标识整个追踪链路。
     *
     * @return 当前线程的追踪ID，如果没有则为null。
     */
    public String currentTraceId() {
        Span span = tracer.currentSpan();
        return nonNull(span) ? span.context().traceId() : null;
    }

    /**
     * 开始一个新的Span，并返回一个ScopedSpan，该ScopedSpan在离开其作用域时会自动结束。
     * 这个方法用于在方法的开始处创建一个新的Span，以追踪方法的执行。
     *
     * @param name 新的Span的名称，用于在追踪系统中标识这个Span。
     * @return 一个ScopedSpan实例，它封装了新创建的Span，并在作用域结束时自动结束该Span。
     */
    public ScopedSpan startNewSpan(String name) {
        return tracer.startScopedSpan(name);
    }

}
