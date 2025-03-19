package org.ricky.core.category.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className SyncCategoryTask
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncCategoryTask implements RepeatableTask {

    private final CategoryRepository categoryRepository;

    public void run(String categoryId, Integer dCnt) {
        Optional<Category> categoryOptional = categoryRepository.byIdOptional(categoryId);
        if (categoryOptional.isEmpty()) {
            return;
        }
        Category category = categoryOptional.get();
        category.incSize(dCnt);
        if (category.isEmpty()) {
            categoryRepository.delete(category);
            return;
        }
        categoryRepository.save(category);
    }

}
