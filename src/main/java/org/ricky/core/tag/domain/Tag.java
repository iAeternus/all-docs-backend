package org.ricky.core.tag.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.core.common.domain.AggregateRoot;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.ricky.common.constants.ConfigConstant.TAG_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.TAG_ID_PREFIX;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className Tag
 * @desc 文档标签
 */
@Getter
@Document(TAG_COLLECTION)
@TypeAlias(TAG_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tag extends AggregateRoot {

    /**
     * 名称
     */
    private String name;

    public Tag(String name) {
        super(newTagId());
        this.name = name;
    }

    public static String newTagId() {
        return TAG_ID_PREFIX + newSnowflakeId();
    }

}
