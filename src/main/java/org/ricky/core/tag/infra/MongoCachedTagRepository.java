package org.ricky.core.tag.infra;

import lombok.extern.slf4j.Slf4j;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.tag.domain.Tag;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import static org.ricky.common.constants.ConfigConstant.TAG_CACHE;
import static org.ricky.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className MongoCachedTagRepository
 * @desc
 */
@Slf4j
@Repository
public class MongoCachedTagRepository extends MongoBaseRepository<Tag> {

    @Cacheable(value = TAG_CACHE, key = "#tagId")
    public Tag cachedById(String tagId) {
        requireNotBlank(tagId, "Tag ID must not be blank.");
        return super.byId(tagId);
    }

    @Caching(evict = {@CacheEvict(value = TAG_CACHE, key = "#tagId")})
    public void evictTagCache(String tagId) {
        requireNotBlank(tagId, "Tag ID must not be blank.");
        log.info("Evicted cache for tag[{}].", tagId);
    }

}
