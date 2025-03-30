package org.ricky.core.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ricky.core.comment.domain.dto.CreateCommentDTO;
import org.ricky.core.comment.service.CommentService;
import org.ricky.core.common.domain.ApiResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.ricky.core.common.domain.ApiResult.success;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentController
 * @desc
 */
@Validated
@CrossOrigin
@RestController
@Tag(name = "评论模块")
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "创建评论")
    public ApiResult<String> create(@RequestBody @Valid CreateCommentDTO dto) {
        return success(commentService.create(dto));
    }

}
