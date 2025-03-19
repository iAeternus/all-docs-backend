package org.ricky.core.category.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.core.category.domain.event.CategoryDeletedEvent;
import org.ricky.core.common.domain.AggregateRoot;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.ricky.common.constants.ConfigConstant.CATEGORY_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.CATEGORY_ID_PREFIX;
import static org.ricky.core.common.domain.OpsLogTypeEnum.UPDATE;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className Category
 * @desc 文档类别
 */
@Getter
@Document(CATEGORY_COLLECTION)
@TypeAlias(CATEGORY_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends AggregateRoot {

    /**
     * 名称
     */
    private String name;

    /**
     * 分类下文档的数量
     */
    private Integer size;

    public Category(String name) {
        super(newCategoryId());
        this.name = name;
        this.size = 0;
    }

    public static String newCategoryId() {
        return CATEGORY_ID_PREFIX + newSnowflakeId();
    }

    public void incSize(Integer cnt) {
        this.size += cnt;
        addOpsLog(UPDATE, "归类 " + cnt + "件文档");
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void onDelete(Boolean isDeleteFile) {
        raiseEvent(new CategoryDeletedEvent(getId(), isDeleteFile));
    }
}
