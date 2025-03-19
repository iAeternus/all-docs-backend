package org.ricky.core.common.domain;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ricky.core.common.context.ThreadLocalContext;
import org.ricky.core.common.context.UserContext;
import org.ricky.core.common.domain.event.DomainEvent;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.ricky.core.common.context.UserContext.of;
import static org.ricky.core.common.domain.AggregateRoot.MAX_OPS_LOG_SIZE;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/1/4
 * @className AggregateRootTest
 * @desc
 */
class AggregateRootTest {

    private static final UserContext TEST_USER = of("USR" + newSnowflakeId(), "username");

    @BeforeAll
    public static void init() {
        ThreadLocalContext.setContext(TEST_USER);
    }

    @AfterAll
    public static void after() {
        ThreadLocalContext.removeContext();
    }

    @Test
    public void should_create() {
        String id = "TEST" + newSnowflakeId();

        TestAggregate aggregate = new TestAggregate(id);
        assertEquals(id, aggregate.getId());
        assertEquals(TEST_USER.getUid(), aggregate.getUserId());

        assertEquals(TEST_USER.getUid(), aggregate.getCreatedBy());
        assertNotNull(aggregate.getCreatedAt());

        assertNull(aggregate.getEvents());
        assertNull(aggregate.getOpsLogs());

        assertEquals(id, aggregate.getId());
    }

    @Test
    public void should_raise_event() {
        String id = "Test" + newSnowflakeId();
        TestAggregate aggregate = new TestAggregate(id);
        DomainEvent event = new DomainEvent() {
        };

        aggregate.raiseEvent(event);
        assertSame(event, aggregate.getEvents().get(0));
    }

    @Test
    public void should_clear_events() {
        String id = "Test" + newSnowflakeId();
        TestAggregate aggregate = new TestAggregate(id);
        DomainEvent event = new DomainEvent() {
        };
        aggregate.raiseEvent(event);
        aggregate.clearEvents();
        assertNull(aggregate.getEvents());
    }

    @Test
    public void should_add_ops_log() {
        String id = "Test" + newSnowflakeId();
        TestAggregate aggregate = new TestAggregate(id);
        String opsLog = "Hello ops logs";
        aggregate.addOpsLog(opsLog);
        assertEquals(opsLog, aggregate.getOpsLogs().get(0).getNote());
    }

    @Test
    public void should_slice_ops_logs_if_too_much() {
        String id = "Test" + newSnowflakeId();
        TestAggregate aggregate = new TestAggregate(id);
        String opsLog = "Hello ops logs";
        IntStream.rangeClosed(0, MAX_OPS_LOG_SIZE).forEach(i -> aggregate.addOpsLog(opsLog));
        String lastLog = "last ops log";
        aggregate.addOpsLog(lastLog);
        assertEquals(MAX_OPS_LOG_SIZE, aggregate.getOpsLogs().size());
        assertEquals(lastLog, aggregate.getOpsLogs().get(MAX_OPS_LOG_SIZE - 1).getNote());
    }

    static class TestAggregate extends AggregateRoot {

        public TestAggregate(String id) {
            super(id);
        }
    }

}