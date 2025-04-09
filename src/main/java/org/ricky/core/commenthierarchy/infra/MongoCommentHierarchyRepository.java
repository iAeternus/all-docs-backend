package org.ricky.core.commenthierarchy.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.exception.MyException;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.commenthierarchy.domain.CommentHierarchyRepository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.ricky.common.exception.ErrorCodeEnum.COMMENT_HIERARCHY_NOT_FOUND;
import static org.ricky.core.common.util.ValidationUtil.isNull;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className MongoCommentHierarchyRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoCommentHierarchyRepository extends MongoBaseRepository<CommentHierarchy> implements CommentHierarchyRepository {
    @Override
    public CommentHierarchy byDocId(String docId) {
        requireNotBlank(docId, "Doc ID must not be blank.");

        Query query = query(where("docId").is(docId));
        CommentHierarchy commentHierarchy = mongoTemplate.findOne(query, CommentHierarchy.class);

        if (isNull(commentHierarchy)) {
            throw new MyException(COMMENT_HIERARCHY_NOT_FOUND, "未找到评论层级", "docId", docId);
        }

        return commentHierarchy;
    }

    @Override
    public void save(CommentHierarchy commentHierarchy) {
        super.save(commentHierarchy);
    }
}
