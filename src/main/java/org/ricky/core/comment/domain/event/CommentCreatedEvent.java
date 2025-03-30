package org.ricky.core.comment.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.ricky.core.common.domain.event.DomainEvent;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.COMMENT_CREATED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentCreatedEvent
 * @desc
 */
@Getter
@TypeAlias("COMMENT_CREATED_EVENT")
@JsonTypeDefine("COMMENT_CREATED_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreatedEvent extends DomainEvent {

    private String commentId;

    public CommentCreatedEvent(String commentId) {
        super(COMMENT_CREATED);
        this.commentId = commentId;
    }
}
