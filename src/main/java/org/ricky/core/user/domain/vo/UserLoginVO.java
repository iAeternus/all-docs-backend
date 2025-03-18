package org.ricky.core.user.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.VO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className UserLoginVO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginVO implements VO {

    String token;
    String userId;
    String username;
    String avatar;
    String type;

}
