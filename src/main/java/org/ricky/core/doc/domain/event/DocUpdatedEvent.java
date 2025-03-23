package org.ricky.core.doc.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_UPDATED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/23
 * @className DocUpdatedEvent
 * @desc TODO 处理器还未编写，后期将移除分类为空就删分类的逻辑
 */
@Getter
@TypeAlias("DOC_UPDATED_EVENT")
@JsonTypeDefine("DOC_UPDATED_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocUpdatedEvent extends DocAwareDomainEvent {

    private String categoryId;
    private List<String> tagIds;

    public DocUpdatedEvent(String docId, String categoryId, List<String> tagIds) {
        super(DOC_UPDATED, docId);
        this.categoryId = categoryId;
        this.tagIds = tagIds;
    }
}
