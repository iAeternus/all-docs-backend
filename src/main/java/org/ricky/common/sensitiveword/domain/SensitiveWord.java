package org.ricky.common.sensitiveword.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.core.common.domain.marker.Identified;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.ricky.common.constants.ConfigConstant.SENSITIVE_WORD_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.SENSITIVE_WORD_ID_PREFIX;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/8/21
 * @className SensitiveWord
 * @desc 敏感词
 */
@Getter
@Document(SENSITIVE_WORD_COLLECTION)
@TypeAlias(SENSITIVE_WORD_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveWord implements Identified {

    /**
     * ID
     */
    private String id;

    /**
     * 敏感词
     */
    private String word;

    public SensitiveWord(String word) {
        this.id = newSensitiveWordId();
        this.word = word;
    }

    public static String newSensitiveWordId() {
        return SENSITIVE_WORD_ID_PREFIX + newSnowflakeId();
    }

    @Override
    public String getId() {
        return id;
    }
}
