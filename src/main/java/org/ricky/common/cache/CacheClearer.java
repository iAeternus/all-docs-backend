package org.ricky.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import static org.ricky.common.constants.ConfigConstant.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/10/14
 * @className CacheClearer
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClearer {

    @Caching(evict = {
            @CacheEvict(value = USER_CACHE, allEntries = true),
            @CacheEvict(value = DOC_CACHE, allEntries = true),
            @CacheEvict(value = TAG_CACHE, allEntries = true),
            @CacheEvict(value = SENSITIVE_WORD_CACHE, allEntries = true),
            @CacheEvict(value = DOC_COMMENT_CACHE, allEntries = true),
    })
    public void evictAllCache() {
        log.info("Evicted all cache.");
    }
}