package org.ricky.core.doc.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import org.ricky.core.common.domain.page.PageDTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/22
 * @className DocPageDTO
 * @desc
 */
@Value
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DocPageDTO extends PageDTO {

    /**
     * 用户id
     */
    @NotBlank
    @Id(pre = USER_ID_PREFIX)
    String userId;

    /**
     * 关键词
     */
    String keyword;

    /**
     * 分类id
     */
    @Id(pre = CATEGORY_ID_PREFIX)
    String categoryId;

    /**
     * 标签id
     */
    @Id(pre = TAG_ID_PREFIX)
    String tagId;

}
