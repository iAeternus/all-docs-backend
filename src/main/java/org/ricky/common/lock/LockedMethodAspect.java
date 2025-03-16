package org.ricky.common.lock;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/11/14
 * @className LockedMethodAspect
 * @desc
 */
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class LockedMethodAspect {

    private final LocalLock localLock;

    @Pointcut("@annotation(org.ricky.common.lock.LockedMethod)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object executeWithLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method tobeLockedMethod = methodSignature.getMethod();
        LockedMethod lockedMethod = tobeLockedMethod.getAnnotation(LockedMethod.class);

        String key = lockedMethod.key();
        long timeout = lockedMethod.timeout();
        TimeUnit unit = lockedMethod.unit();

        return localLock.executeWithLock(key, timeout, unit, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

}
