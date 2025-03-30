package org.ricky.core.comment.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CreateCommentDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCommentDTO implements DTO {

    /**
     * 文档ID
     */
    @NotBlank
    @Id(pre = DOC_ID_PREFIX)
    String docId;

    /**
     * 父评论ID，可以为空
     */
    @Id(pre = COMMENT_ID_PREFIX)
    String parentCommentId;

    /**
     * 评论内容
     */
    @NotBlank
    @Size(max = MAX_GENERIC_TEXT_LENGTH)
    String content;

}
