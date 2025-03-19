package org.ricky.core.doc.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_DELETED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className DocDeletedEvent
 * @desc
 */
@Getter
@TypeAlias("DOC_DELETED_EVENT")
@JsonTypeDefine("DOC_DELETED_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocDeletedEvent extends DocAwareDomainEvent {

    private String categoryId;

    public DocDeletedEvent(String docId, String categoryId) {
        super(DOC_DELETED, docId);
        this.categoryId = categoryId;
    }

}
