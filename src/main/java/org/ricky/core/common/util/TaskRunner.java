package org.ricky.core.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className TaskRunner
 * @desc 任务运行器
 * TODO 支持异步运行任务
 */
@Slf4j
public class TaskRunner {

    /**
     * 是否发生错误
     */
    private boolean hasError;

    public static TaskRunner newTaskRunner() {
        return new TaskRunner();
    }

    public void run(Runnable runnable) {
        try {
            runnable.run(); // TODO 优化为异步
        } catch (Throwable t) {
            log.error("Failed to run task: ", t);
            hasError = true;
        }
    }

    public void join() {
        // TODO
    }

    public boolean hasError() {
        return hasError;
    }

}
