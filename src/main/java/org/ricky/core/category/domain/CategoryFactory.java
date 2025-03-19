package org.ricky.core.category.domain;

import org.ricky.core.category.domain.vo.CategoryVO;
import org.springframework.stereotype.Component;

import static org.ricky.core.common.util.ValidationUtil.isNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryFactory
 * @desc
 */
@Component
public class CategoryFactory {

    public CategoryVO category2vo(Category category) {
        if (isNull(category)) {
            return null;
        }
        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
