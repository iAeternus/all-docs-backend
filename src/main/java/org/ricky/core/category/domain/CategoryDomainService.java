package org.ricky.core.category.domain;

import lombok.RequiredArgsConstructor;
import org.ricky.common.exception.MyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ricky.common.exception.ErrorCodeEnum.CATEGORY_NAME_ALREADY_EXISTS;
import static org.ricky.core.common.util.ValidationUtil.isNotEmpty;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryDomainService
 * @desc
 */
@Service
@RequiredArgsConstructor
public class CategoryDomainService {

    private final CategoryRepository categoryRepository;

    public Category create(String name) {
        List<Category> names = categoryRepository.listByName(name);
        if (isNotEmpty(names)) {
            throw new MyException(CATEGORY_NAME_ALREADY_EXISTS, "分类已存在",
                    Map.of("name", name));
        }

        return new Category(name);
    }
}
