package org.ricky.core.doc.domain.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_SEARCHED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className DocSearchedEvent
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocSearchedEvent extends DocAwareDomainEvent {

    public DocSearchedEvent(String docId) {
        super(DOC_SEARCHED, docId);
    }

}
