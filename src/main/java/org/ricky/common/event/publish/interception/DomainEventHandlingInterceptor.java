package org.ricky.common.event.publish.interception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.event.publish.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className DomainEventHandlingInterceptor
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventHandlingInterceptor implements HandlerInterceptor {

    private final DomainEventPublisher domainEventPublisher;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        // 确保开始处理请求时，holder中没有其它事件ID
        ThreadLocalDomainEventIdHolder.clear();
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           ModelAndView modelAndView) throws Exception {
        List<String> eventIds = ThreadLocalDomainEventIdHolder.allEventIds();
        try {
            domainEventPublisher.publish(eventIds);
        } finally {
            ThreadLocalDomainEventIdHolder.remove();
        }
    }
}
