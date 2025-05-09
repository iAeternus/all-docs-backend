package org.ricky.core.comment.service;

import org.ricky.core.comment.domain.dto.CreateCommentDTO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentService
 * @desc
 */
public interface CommentService {
    String create(CreateCommentDTO dto);
}
