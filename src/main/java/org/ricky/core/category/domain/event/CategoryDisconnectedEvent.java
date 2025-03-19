package org.ricky.core.category.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.ricky.core.common.domain.event.DomainEvent;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.CATEGORY_DISCONNECTED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDisconnectedEvent
 * @desc
 */
@Getter
@TypeAlias("CATEGORY_DISCONNECT_EVENT")
@JsonTypeDefine("CATEGORY_DISCONNECT_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDisconnectedEvent extends DomainEvent {

    private String categoryId;
    private Integer dCnt;

    public CategoryDisconnectedEvent(String categoryId, Integer dCnt) {
        super(CATEGORY_DISCONNECTED);
        this.categoryId = categoryId;
        this.dCnt = dCnt;
    }
}
