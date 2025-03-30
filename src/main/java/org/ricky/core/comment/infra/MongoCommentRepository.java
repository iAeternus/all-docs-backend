package org.ricky.core.comment.infra;

import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.comment.domain.Comment;
import org.ricky.core.comment.domain.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className MongoCommentRepository
 * @desc
 */
@Repository
public class MongoCommentRepository extends MongoBaseRepository<Comment> implements CommentRepository {
    @Override
    public Set<String> fuzzyByKeyword(String keyword) {
        // TODO
        return Set.of();
    }

    @Override
    public void save(Comment comment) {
        super.save(comment);
    }

    @Override
    public Comment byId(String id) {
        return super.byId(id);
    }
}
