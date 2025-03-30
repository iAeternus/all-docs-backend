package org.ricky.core.commenthierarchy.service;

import org.ricky.core.commenthierarchy.domain.vo.CommentHierarchyVO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyService
 * @desc
 */
public interface CommentHierarchyService {
    CommentHierarchyVO fetchCommentHierarchy(String docId);
}
