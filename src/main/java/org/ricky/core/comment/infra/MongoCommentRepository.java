package org.ricky.core.comment.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.comment.domain.Comment;
import org.ricky.core.comment.domain.CommentRepository;
import org.ricky.core.comment.domain.DocCachedComment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className MongoCommentRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoCommentRepository extends MongoBaseRepository<Comment> implements CommentRepository {

    private final MongoCachedCommentRepository cachedCommentRepository;

    @Override
    public Set<String> fuzzyByKeyword(String keyword) {
        // TODO
        return Set.of();
    }

    @Override
    public void save(Comment comment) {
        super.save(comment);
        cachedCommentRepository.evictCommentCache(comment.getDocId());
    }

    @Override
    public Comment byId(String id) {
        return super.byId(id);
    }

    @Override
    public List<DocCachedComment> cachedDocAllComments(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");

        return cachedCommentRepository.cachedDocAllComments(docId);
    }
}
