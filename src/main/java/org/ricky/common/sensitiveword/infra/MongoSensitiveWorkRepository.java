package org.ricky.common.sensitiveword.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.sensitiveword.domain.SensitiveWord;
import org.ricky.common.sensitiveword.domain.SensitiveWordRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className MongoSensitiveWorkRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoSensitiveWorkRepository implements SensitiveWordRepository {

    private final MongoTemplate mongoTemplate;
    private final MongoCachedSensitiveWordRepository cachedSensitiveWordRepository;

    @Override
    public void insertBatch(List<SensitiveWord> sensitiveWords) {
        mongoTemplate.insert(sensitiveWords, SensitiveWord.class);
        cachedSensitiveWordRepository.evictAll();
    }

    @Override
    public List<SensitiveWord> listAll() {
        return cachedSensitiveWordRepository.cachedAll();
    }
}
