package org.ricky.common.lock;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/11/14
 * @className LocalLock
 * @desc 加锁的模板代码
 */
@Component
public class LocalLock {

    private final Cache<String, Lock> lockCache;

    public LocalLock() {
        this.lockCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, MINUTES)
                .build();
    }

    public void executeWithLock(String key, long timeout, TimeUnit unit, Runnable task) throws InterruptedException {
        Lock lock = getLock(key);
        boolean locked = lock.tryLock(timeout, unit);
        if (!locked) {
            return;
        }

        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }

    public <T> T executeWithLock(String key, long timeout, TimeUnit unit, Supplier<T> task) throws InterruptedException {
        Lock lock = getLock(key);
        boolean locked = lock.tryLock(timeout, unit);
        if (!locked) {
            return null;
        }

        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    private Lock getLock(String key) {
        Lock lock = lockCache.getIfPresent(key);
        if (lock == null) {
            synchronized (this) {
                lock = lockCache.getIfPresent(key);
                if (lock == null) {
                    lock = new ReentrantLock();
                    lockCache.put(key, lock);
                }
            }
        }
        return lock;
    }

}
