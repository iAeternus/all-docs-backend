package org.ricky.core.comment.domain;

import org.ricky.common.exception.MyException;

import static org.ricky.common.constants.ConfigConstant.COMMENT_ID_PREFIX;
import static org.ricky.common.constants.ConfigConstant.DOC_ID_PREFIX;
import static org.ricky.common.exception.ErrorCodeEnum.INVALID_ID_PREFIX;
import static org.ricky.core.common.validation.id.IdValidator.isId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentTypeEnum
 * @desc 评论类型
 */
@Deprecated
public enum CommentTypeEnum {

    DOC,
    COMMENT,
    ;

    public static CommentTypeEnum of(String parentId) {
        if (isId(parentId, DOC_ID_PREFIX)) {
            return DOC;
        } else if (isId(parentId, COMMENT_ID_PREFIX)) {
            return COMMENT;
        } else {
            throw new MyException(INVALID_ID_PREFIX, "父节点ID前缀不合法", "parentId", parentId);
        }
    }

}
