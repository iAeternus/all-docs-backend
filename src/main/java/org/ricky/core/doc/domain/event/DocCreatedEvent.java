package org.ricky.core.doc.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_CREATED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocCreatedEvent
 * @desc
 */
@Getter
@TypeAlias("DOC_CREATED_EVENT")
@JsonTypeDefine("DOC_CREATED_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocCreatedEvent extends DocAwareDomainEvent {

    private String suffix;

    public DocCreatedEvent(String docId, String suffix) {
        super(DOC_CREATED, docId);
        this.suffix = suffix;
    }
}
