package org.ricky.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className SetUpResponse
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SetUpResponse {

    String userId;
    String token;

}
