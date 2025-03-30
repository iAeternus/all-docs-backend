package org.ricky.core.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.common.sensitiveword.service.SensitiveWordService;
import org.ricky.core.comment.domain.Comment;
import org.ricky.core.comment.domain.CommentFactory;
import org.ricky.core.comment.domain.CommentRepository;
import org.ricky.core.comment.domain.dto.CreateCommentDTO;
import org.ricky.core.comment.service.CommentService;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.commenthierarchy.domain.CommentHierarchyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.ricky.common.ratelimit.TPSConstants.MINIMUM_TPS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentServiceImpl
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final RateLimiter rateLimiter;
    private final SensitiveWordService sensitiveWordService;
    private final CommentRepository commentRepository;
    private final CommentHierarchyRepository commentHierarchyRepository;
    private final CommentFactory commentFactory;

    @Override
    @Transactional
    public String create(CreateCommentDTO dto) {
        rateLimiter.applyFor("Comment:Create", MINIMUM_TPS);

        String content = sensitiveWordService.filter(dto.getContent());

        CommentHierarchy commentHierarchy = commentHierarchyRepository.byDocId(dto.getDocId());
        Comment comment = commentFactory.create(content);
        commentHierarchy.addComment(dto.getParentCommentId(), comment.getId());

        commentRepository.save(comment);
        commentHierarchyRepository.save(commentHierarchy);
        log.info("Created comment[{}]", comment.getId());
        return comment.getId();
    }
}
