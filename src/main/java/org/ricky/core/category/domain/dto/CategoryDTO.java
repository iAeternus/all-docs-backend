package org.ricky.core.category.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;

import static org.ricky.common.constants.ConfigConstant.MAX_GENERIC_NAME_LENGTH;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDTO implements DTO {

    @NotBlank
    @Size(max = MAX_GENERIC_NAME_LENGTH)
    String name;

}
