package org.ricky.core.doc.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;
import org.ricky.core.common.validation.collection.NoNullElement;
import org.ricky.core.common.validation.id.Id;

import java.util.List;

import static org.ricky.common.constants.ConfigConstant.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/23
 * @className UpdateDocDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateDocDTO implements DTO {

    /**
     * ID
     */
    @NotNull
    @Id(pre = DOC_ID_PREFIX)
    String docId;

    /**
     * 文档名称
     */
    @Size(min = 1, max = MAX_GENERIC_NAME_LENGTH)
    String name;

    /**
     * 文档分类ID
     */
    @Id(pre = CATEGORY_ID_PREFIX)
    String categoryId;

    /**
     * 文档标签集合
     */
    @NoNullElement
    List<@Id(pre = TAG_ID_PREFIX) String> tagIds;

    /**
     * 文档描述信息
     */
    @Size(max = MAX_GENERIC_TEXT_LENGTH)
    String desc;

}
