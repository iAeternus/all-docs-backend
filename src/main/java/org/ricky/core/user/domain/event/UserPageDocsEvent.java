package org.ricky.core.user.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.ricky.core.common.domain.event.DomainEvent;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.USER_PAGE_DOCS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className UserPageDocsEvent
 * @desc
 */
@Getter
@TypeAlias("USER_PAGE_DOCS_EVENT")
@JsonTypeDefine("USER_PAGE_DOCS_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPageDocsEvent extends DomainEvent {

    private String userId;
    private String keyword;

    public UserPageDocsEvent(String userId, String keyword) {
        super(USER_PAGE_DOCS);
        this.userId = userId;
        this.keyword = keyword;
    }
}
