package org.ricky.common.mongo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventDao;
import org.ricky.core.common.domain.event.DomainEventTypeEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Objects.requireNonNull;
import static org.ricky.common.exception.ErrorCodeEnum.DOMAIN_EVENT_NOT_FOUND;
import static org.ricky.common.util.ValidationUtil.isNull;
import static org.ricky.common.util.ValidationUtil.requireNotBlank;
import static org.ricky.core.common.domain.event.DomainEventStatusEnum.*;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/10
 * @className MongoDomainEventDao
 * @desc 领域事件数据访问对象
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoDomainEventDao implements DomainEventDao {

    private final MongoTemplate mongoTemplate;

    @Override
    public void insert(List<DomainEvent> events) {
        Objects.requireNonNull(events, "Domain events must not be null.");

        mongoTemplate.insertAll(events);
    }

    @Override
    public DomainEvent byId(String id) {
        requireNotBlank(id, "Domain event ID must not be blank.");

        DomainEvent domanEvent = mongoTemplate.findOne(query(where("_id").is(id)), DomainEvent.class);
        if (isNull(domanEvent)) {
            throw new MyException(DOMAIN_EVENT_NOT_FOUND, "未找到领域事件。", of("eventId", id));
        }

        return domanEvent;
    }

    @Override
    public List<DomainEvent> byIds(List<String> ids) {
        requireNonNull(ids, "Domain event IDs must not be blank.");

        return mongoTemplate.find(query(Criteria.where("_id").in(ids))
                .with(Sort.by(ASC, "raisedAt")), DomainEvent.class);
    }

    @Override
    public <T extends DomainEvent> T latestEventFor(String arId, DomainEventTypeEnum type, Class<T> eventClass) {
        requireNotBlank(arId, "AR ID must not be blank.");
        requireNonNull(type, "Domain event type must not be null.");
        requireNonNull(eventClass, "Domain event class must not be null.");

        Query query = query(where("arId").is(arId).and("type").is(type)).with(Sort.by(DESC, "raisedAt"));
        return mongoTemplate.findOne(query, eventClass);
    }

    @Override
    public void successPublish(DomainEvent event) {
        requireNonNull(event, "Domain event must not be null.");

        Query query = query(where("_id").is(event.getId()));
        Update update = new Update();
        update.set("status", PUBLISH_SUCCEED.name()).inc("publishedCount");
        mongoTemplate.updateFirst(query, update, DomainEvent.class);
    }

    @Override
    public void failPublish(DomainEvent event) {
        requireNonNull(event, "Domain event must not be null.");

        Query query = query(where("_id").is(event.getId()));
        Update update = new Update();
        update.set("status", PUBLISH_FAILED.name()).inc("publishedCount");
        mongoTemplate.updateFirst(query, update, DomainEvent.class);
    }

    @Override
    public void successConsume(DomainEvent event) {
        requireNonNull(event, "Domain event must not be null.");

        Query query = query(where("_id").is(event.getId()));
        Update update = new Update();
        update.set("status", CONSUME_SUCCEED.name()).inc("consumedCount");
        mongoTemplate.updateFirst(query, update, DomainEvent.class);
    }

    @Override
    public void failConsume(DomainEvent event) {
        requireNonNull(event, "Domain event must not be null.");

        Query query = query(where("_id").is(event.getId()));
        Update update = new Update();
        update.set("status", CONSUME_FAILED.name()).inc("consumedCount");
        mongoTemplate.updateFirst(query, update, DomainEvent.class);
    }

    @Override
    public List<DomainEvent> tobePublishedEvents(String startId, int limit) {
        requireNotBlank(startId, "Start ID must not be blank.");

        Query query = query(where("status").in(CREATED, PUBLISH_FAILED, CONSUME_FAILED)
                .and("_id").gt(startId)
                .and("publishedCount").lt(3)
                .and("consumedCount").lt(3))
                .with(Sort.by(ASC, "raisedAt"))
                .limit(limit);
        return mongoTemplate.find(query, DomainEvent.class);
    }

}
