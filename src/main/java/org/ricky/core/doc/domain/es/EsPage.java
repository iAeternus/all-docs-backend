package org.ricky.core.doc.domain.es;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.ValueObject;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className EsPage
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EsPage implements ValueObject {

    Integer order;
    String content;
    Integer page;

}
