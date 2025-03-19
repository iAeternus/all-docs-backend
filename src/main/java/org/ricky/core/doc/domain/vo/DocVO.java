package org.ricky.core.doc.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.category.domain.vo.CategoryVO;
import org.ricky.core.common.domain.marker.VO;
import org.ricky.core.doc.domain.DocStatusEnum;
import org.ricky.core.doc.domain.es.EsPage;
import org.ricky.core.tag.domain.vo.TagVO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className DocVO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocVO implements VO {

    String id;
    String name;
    Long size;
    String desc;
    Integer collectCnt; // TODO
    Integer commentCnt; // TODO
    CategoryVO categoryVO;
    String thumbId;
    String txtId;
    String previewFileId;
    DocStatusEnum status;
    String errorMsg;
    List<TagVO> tagVOs;
    List<EsPage> esPages; // TODO
    LocalDateTime uploadAt;
    Instant updateAt;
    String updateBy;

}
