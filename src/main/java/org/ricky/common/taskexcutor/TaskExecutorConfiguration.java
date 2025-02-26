package org.ricky.common.taskexcutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/11
 * @className TaskExecutorConfiguration
 * @desc
 */
@Configuration
public class TaskExecutorConfiguration {

    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.initialize();
        executor.setThreadNamePrefix("all-docs-common-");
        return executor;
    }

    public static TaskExecutor consumeEventTaskExecutor(String identifier) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(5000);
        executor.initialize();
        executor.setThreadNamePrefix(String.format("all-docs-%s-", identifier));
        return executor;
    }

}
