package org.ricky.core.category.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className DisConnectDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisConnectDTO implements DTO {

    String docId;
    String categoryId;

}
