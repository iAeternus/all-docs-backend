package org.ricky.core.doc.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;
import org.ricky.core.common.validation.id.Id;

import static org.ricky.common.constants.ConfigConstant.DOC_ID_PREFIX;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className RemoveDocDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveDocDTO implements DTO {

    @NotBlank
    @Id(pre = DOC_ID_PREFIX)
    String docId;

    @NotNull
    Boolean isDeleteFile;

    public Boolean isDeleteFile() {
        return isDeleteFile;
    }

}
