package org.ricky.core.commenthierarchy.service.impl;

import lombok.RequiredArgsConstructor;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.core.comment.domain.CommentRepository;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.commenthierarchy.domain.CommentHierarchyRepository;
import org.ricky.core.commenthierarchy.domain.vo.CommentHierarchyVO;
import org.ricky.core.commenthierarchy.service.CommentHierarchyService;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.ricky.common.ratelimit.TPSConstants.LOW_TPS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyServiceImpl
 * @desc
 */
@Service
@RequiredArgsConstructor
public class CommentHierarchyServiceImpl implements CommentHierarchyService {

    private final RateLimiter rateLimiter;
    private final CommentHierarchyRepository commentHierarchyRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentHierarchyVO fetchCommentHierarchy(String docId) {
        rateLimiter.applyFor("CommentHierarchy:FetchCommentHierarchy", LOW_TPS);

        CommentHierarchy commentHierarchy = commentHierarchyRepository.byDocId(docId);
        List<CommentHierarchyVO.CommentVO> allComments = commentRepository.cachedDocAllComments(docId).stream()
                .map(comment -> CommentHierarchyVO.CommentVO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .build())
                .collect(toImmutableList());

        return CommentHierarchyVO.builder()
                .idTree(commentHierarchy.getIdTree())
                .allComments(allComments)
                .build();
    }
}
