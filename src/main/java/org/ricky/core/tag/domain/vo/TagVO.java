package org.ricky.core.tag.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.VO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className TagVO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TagVO implements VO {

    String id;
    String name;

}
