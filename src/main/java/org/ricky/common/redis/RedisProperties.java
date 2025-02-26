package org.ricky.common.redis;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className RedisProperties
 * @desc
 */
@Data
@Component
@Validated
@ConfigurationProperties("all-docs.redis")
public class RedisProperties {

    /**
     * 领域事件流
     */
    @NotBlank
    private String domainEventStream;

    /**
     * 通知流
     */
    @NotBlank
    private String notificationStream;

    /**
     * Webhook流
     */
    @NotBlank
    private String webhookStream;

}
