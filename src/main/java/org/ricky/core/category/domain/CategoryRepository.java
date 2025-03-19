package org.ricky.core.category.domain;

import java.util.List;
import java.util.Optional;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryRepository
 * @desc
 */
public interface CategoryRepository {
    List<Category> listByName(String name);

    void save(Category category);

    Category byId(String categoryId);

    Optional<Category> byIdOptional(String categoryId);

    boolean exists(String categoryId);

    void delete(Category category);
}
