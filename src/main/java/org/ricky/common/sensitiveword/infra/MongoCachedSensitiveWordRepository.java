package org.ricky.common.sensitiveword.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.sensitiveword.domain.SensitiveWord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ricky.common.constants.ConfigConstant.SENSITIVE_WORD_CACHE;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className MongoCachedSensitiveWordRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoCachedSensitiveWordRepository {

    private final MongoTemplate mongoTemplate;

    @Cacheable(value = SENSITIVE_WORD_CACHE)
    public List<SensitiveWord> cachedAll() {
        return mongoTemplate.findAll(SensitiveWord.class);
    }

    @Caching(evict = {@CacheEvict(value = SENSITIVE_WORD_CACHE, allEntries = true)})
    public void evictAll() {
    }

}
