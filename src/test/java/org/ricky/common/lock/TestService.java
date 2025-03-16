package org.ricky.common.lock;

import lombok.Getter;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/11/14
 * @className TestService
 * @desc
 */
@Getter
@Service
public class TestService {

    private int counter = 0;

    public void method() {
        System.out.println("Do something here...");
    }

    public int method2() {
        System.out.println("Do something here...");
        return 0;
    }

    @LockedMethod(timeout = 1, unit = SECONDS)
    public int method3() {
        System.out.println("Do something here...");
        return 0;
    }

    @LockedMethod(key = "TestService:increment", timeout = 1, unit = SECONDS)
    public void increment() {
        this.counter++;
    }

}
