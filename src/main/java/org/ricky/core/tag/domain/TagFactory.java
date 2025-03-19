package org.ricky.core.tag.domain;

import org.ricky.core.tag.domain.vo.TagVO;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className TagFactory
 * @desc
 */
@Component
public class TagFactory {

    public TagVO tag2vo(Tag tag) {
        return TagVO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

}
