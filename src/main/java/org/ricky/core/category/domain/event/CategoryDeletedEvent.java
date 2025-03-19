package org.ricky.core.category.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.ricky.core.common.domain.event.DomainEvent;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.CATEGORY_DELETED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDeletedEvent
 * @desc
 */
@Getter
@TypeAlias("CATEGORY_DELETED_EVENT")
@JsonTypeDefine("CATEGORY_DELETED_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDeletedEvent extends DomainEvent {

    private String categoryId;
    private Boolean isDeleteFile;

    public CategoryDeletedEvent(String categoryId, Boolean isDeleteFile) {
        super(CATEGORY_DELETED);
        this.categoryId = categoryId;
        this.isDeleteFile = isDeleteFile;
    }
}
