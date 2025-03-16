package org.ricky.core.doc.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.core.common.domain.event.DomainEvent;
import org.ricky.core.common.domain.event.DomainEventTypeEnum;

import static lombok.AccessLevel.PROTECTED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocAwareDomainEvent
 * @desc
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
public abstract class DocAwareDomainEvent extends DomainEvent {

    protected String docId;

    public DocAwareDomainEvent(DomainEventTypeEnum type, String docId) {
        super(type);
        this.docId = docId;
    }
}
