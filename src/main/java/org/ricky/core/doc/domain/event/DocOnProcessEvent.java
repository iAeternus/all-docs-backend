package org.ricky.core.doc.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.json.JsonTypeDefine;
import org.springframework.data.annotation.TypeAlias;

import static org.ricky.core.common.domain.event.DomainEventTypeEnum.DOC_ON_PROCESS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocOnProcessEvent
 * @desc
 */
@Getter
@TypeAlias("DOC_ON_PROCESS_EVENT")
@JsonTypeDefine("DOC_ON_PROCESS_EVENT")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocOnProcessEvent extends DocAwareDomainEvent {

    private String gridFsId;
    private String textFileId;
    private String previewFileId;
    private String thumbId;

    public DocOnProcessEvent(String docId, String gridFsId, String textFileId, String previewFileId, String thumbId) {
        super(DOC_ON_PROCESS, docId);
        this.gridFsId = gridFsId;
        this.textFileId = textFileId;
        this.previewFileId = previewFileId;
        this.thumbId = thumbId;
    }
}
