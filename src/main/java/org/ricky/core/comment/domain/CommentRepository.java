package org.ricky.core.comment.domain;

import java.util.List;
import java.util.Set;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className CommentRepository
 * @desc
 */
public interface CommentRepository {
    Set<String> fuzzyByKeyword(String keyword);

    void save(Comment comment);

    Comment byId(String commentId);

    List<DocCachedComment> cachedDocAllComments(String docId);
}
