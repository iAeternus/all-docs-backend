package org.ricky.core.user.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.domain.DTO;
import org.ricky.core.common.validation.collection.NoNullElement;
import org.ricky.core.common.validation.id.Id;

import java.util.List;

import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className DeleteByIdBatchDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteByIdBatchDTO implements DTO {

    @NotNull
    @NoNullElement
    @Size(min = 1)
    List<@Id(pre = USER_ID_PREFIX) String> ids;

}
