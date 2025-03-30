package org.ricky.core.comment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.core.comment.domain.event.CommentCreatedEvent;
import org.ricky.core.common.domain.AggregateRoot;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.ricky.common.constants.ConfigConstant.COMMENT_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.COMMENT_ID_PREFIX;
import static org.ricky.core.common.domain.OpsLogTypeEnum.CREATE;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className Comment
 * @desc 评论
 */
@Getter
@Document(COMMENT_COLLECTION)
@TypeAlias(COMMENT_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends AggregateRoot {

    /**
     * 所在的文档ID
     */
    private String docId;

    /**
     * 评论内容
     */
    private String content;

    public Comment(String docId, String content) {
        super(newCommentId());
        this.docId = docId;
        this.content = content;
        raiseEvent(new CommentCreatedEvent(getId()));
        addOpsLog(CREATE, "新建");
    }

    public static String newCommentId() {
        return COMMENT_ID_PREFIX + newSnowflakeId();
    }

}
