package org.ricky.common.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className LocalLockTest
 * @desc
 */
@SpringBootTest
class LocalLockTest {

    @Autowired
    private TestService testService;

    @Autowired
    private LocalLock localLock;

    @Test
    void should_execute_with_lock() throws InterruptedException {
        localLock.executeWithLock("TestService:method", 1, MINUTES, testService::method);
    }

    @Test
    void should_execute_with_lock_2() throws InterruptedException {
        Integer res = localLock.executeWithLock("TestService:method2", 1, MINUTES, testService::method2);
        assertEquals(0, res);
    }

    @Test
    void should_execute_with_lock_3() {
        assertEquals(0, testService.method3());
    }

    @Test
    void should_execute_with_lock_4() throws InterruptedException {
        int n = 1000;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < n; ++i) {
                testService.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < n; ++i) {
                testService.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(n << 1, testService.getCounter());
    }

}