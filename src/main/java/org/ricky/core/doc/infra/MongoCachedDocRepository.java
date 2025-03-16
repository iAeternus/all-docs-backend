package org.ricky.core.doc.infra;

import lombok.extern.slf4j.Slf4j;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.doc.domain.Doc;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import static org.ricky.common.constants.ConfigConstant.DOC_CACHE;
import static org.ricky.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className MongoCachedDocRepository
 * @desc
 */
@Slf4j
@Repository
public class MongoCachedDocRepository extends MongoBaseRepository<Doc> {

    @Cacheable(value = DOC_CACHE, key = "#docId")
    public Doc cachedById(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");
        return super.byId(docId);
    }

    @Caching(evict = {@CacheEvict(value = DOC_CACHE, key = "#docId")})
    public void evictDocCache(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");
        log.info("Evicted cache for doc[{}].", docId);
    }

    @Caching(evict = {@CacheEvict(value = DOC_CACHE, allEntries = true)})
    public void evictAll() {
    }

}
