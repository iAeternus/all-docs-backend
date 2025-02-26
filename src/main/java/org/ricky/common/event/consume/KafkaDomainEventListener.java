package org.ricky.common.event.consume;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className KafkaDomainEventListener
 * @desc Kafka事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDomainEventListener implements DomainEventListener {
}
