package org.ricky.core.category.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.CATEGORY_ID_PREFIX;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className RemoveCategoryDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveCategoryDTO implements DTO {

    @NotBlank
    @Id(pre = CATEGORY_ID_PREFIX)
    String categoryId;

    @NotNull
    Boolean isDeleteFile;

}
