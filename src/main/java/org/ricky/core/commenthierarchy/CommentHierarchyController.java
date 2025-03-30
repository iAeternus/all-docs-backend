package org.ricky.core.commenthierarchy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.ricky.core.commenthierarchy.domain.vo.CommentHierarchyVO;
import org.ricky.core.commenthierarchy.service.CommentHierarchyService;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.common.validation.id.Id;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.ricky.common.constants.ConfigConstant.DOC_ID_PREFIX;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyController
 * @desc
 */
@Validated
@CrossOrigin
@RestController
@Tag(name = "评论层次结构模块")
@RequiredArgsConstructor
@RequestMapping("/comment_hierarchies")
public class CommentHierarchyController {

    private final CommentHierarchyService commentHierarchyService;

    @GetMapping("/comments/{docId}")
    @Operation(summary = "获取文档下所有评论")
    public ApiResult<CommentHierarchyVO> fetchCommentHierarchy(@PathVariable("docId") @NotBlank @Id(pre = DOC_ID_PREFIX) String docId) {
        return ApiResult.success(commentHierarchyService.fetchCommentHierarchy(docId));
    }

}
