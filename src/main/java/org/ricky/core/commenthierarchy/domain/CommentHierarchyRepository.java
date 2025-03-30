package org.ricky.core.commenthierarchy.domain;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyRepository
 * @desc
 */
public interface CommentHierarchyRepository {
    CommentHierarchy byDocId(String docId);

    void save(CommentHierarchy commentHierarchy);

}
