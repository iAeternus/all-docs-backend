package org.ricky.management;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.cache.CacheClearer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import static java.util.Locale.CHINESE;
import static org.ricky.common.constants.ConfigConstant.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.CollectionOptions.just;
import static org.springframework.data.mongodb.core.query.Collation.of;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className AllDocsSystemInitializer
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AllDocsSystemInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final CacheClearer cacheClearer;
    private final MongoTemplate mongoTemplate;
    private final SystemAdmin systemAdmin;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        cacheClearer.evictAllCache();
        ensureMongoCollectionExist();
        ensureMongoIndexExist();
        ensureAllDocsManageAppsExist();
        log.info("All-Docs system initialized.");
    }

    private void ensureMongoCollectionExist() {
        createCollection(EVENT_COLLECTION);
        createCollection(USER_COLLECTION);
        createCollection(DOC_COLLECTION);
        createCollection(TAG_COLLECTION);
        createCollection(CATEGORY_COLLECTION);
        createCollection(SENSITIVE_WORD_COLLECTION);
        createCollection(COMMENT_COLLECTION);
        createCollection(COMMENT_HIERARCHY_COLLECTION);
    }

    private void createCollection(String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName, just(of(CHINESE).numericOrderingEnabled()));
        }
    }

    private void ensureMongoIndexExist() {
        ensureDomainEventIndex();
    }

    private void ensureDomainEventIndex() {
        IndexOperations indexOperations = mongoTemplate.indexOps(EVENT_COLLECTION);
        indexOperations.ensureIndex(new Index().on("status", DESC));
        indexOperations.ensureIndex(new Index().on("publishedCount", DESC));
        indexOperations.ensureIndex(new Index().on("consumedCount", DESC));
        indexOperations.ensureIndex(new Index().on("raisedAt", DESC));
    }

    private void ensureAllDocsManageAppsExist() {
        // 系统管理
        systemAdmin.init();
    }

}
