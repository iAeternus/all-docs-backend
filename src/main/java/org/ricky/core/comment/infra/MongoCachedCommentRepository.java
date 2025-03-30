package org.ricky.core.comment.infra;

import lombok.extern.slf4j.Slf4j;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.comment.domain.Comment;
import org.ricky.core.comment.domain.DocCachedComment;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.ricky.common.constants.ConfigConstant.COMMENT_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.DOC_COMMENT_CACHE;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className MongoCachedCommentRepository
 * @desc
 */
@Slf4j
@Repository
public class MongoCachedCommentRepository extends MongoBaseRepository<Comment> {

    @Cacheable(value = DOC_COMMENT_CACHE, key = "#docId")
    public List<DocCachedComment> cachedDocAllComments(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");

        Query query = query(where("docId").is(docId));
        query.fields().include("content");
        return new ArrayList<>(mongoTemplate.find(query, DocCachedComment.class, COMMENT_COLLECTION));
    }

    @Caching(evict = {@CacheEvict(value = DOC_COMMENT_CACHE, key = "#docId")})
    public void evictCommentCache(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");

        log.info("Evicted cache for comment[{}].", docId);
    }

}
