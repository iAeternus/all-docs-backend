package org.ricky.core.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.ricky.common.exception.MyException;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryDomainService;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.category.domain.dto.DisConnectDTO;
import org.ricky.core.category.domain.dto.RemoveCategoryDTO;
import org.ricky.core.category.domain.vo.CategoryVO;
import org.ricky.core.category.service.CategoryService;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.ricky.common.exception.ErrorCodeEnum.CATEGORY_NOT_FOUND;
import static org.ricky.common.ratelimit.TPSConstants.MINIMUM_TPS;
import static org.ricky.common.ratelimit.TPSConstants.NORMAL_TPS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryServiceImpl
 * @desc
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final RateLimiter rateLimiter;
    private final CategoryDomainService categoryDomainService;
    private final CategoryRepository categoryRepository;
    private final DocRepository docRepository;

    @Override
    public String create(CategoryDTO dto) {
        rateLimiter.applyFor("Category:Create", MINIMUM_TPS);

        Category category = categoryDomainService.create(dto.getName());
        categoryRepository.save(category);

        return category.getId();
    }

    @Override
    public CategoryVO getById(String categoryId) {
        rateLimiter.applyFor("Category:GetById", NORMAL_TPS);

        Category category = categoryRepository.byId(categoryId);

        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    @Transactional
    public Boolean connect(ConnectDTO dto) {
        rateLimiter.applyFor("Category:Connect", NORMAL_TPS);

        Optional<Category> categoryOptional = categoryRepository.byIdOptional(dto.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new MyException(CATEGORY_NOT_FOUND, "分类未找到", Map.of("categoryId", dto.getCategoryId()));
        }

        Category category = categoryOptional.get();
        category.incSize(1);
        categoryRepository.save(category);

        Doc doc = docRepository.cachedById(dto.getDocId());
        doc.connectWithCategory(dto.getCategoryId());
        docRepository.save(doc);

        return true;
    }

    @Override
    public Boolean disconnect(DisConnectDTO dto) {
        rateLimiter.applyFor("Category:Connect", NORMAL_TPS);

        if (!categoryRepository.exists(dto.getCategoryId())) {
            throw new MyException(CATEGORY_NOT_FOUND, "分类未找到", Map.of("categoryId", dto.getCategoryId()));
        }

        Doc doc = docRepository.cachedById(dto.getDocId());
        doc.disconnectWithCategory();
        docRepository.save(doc);

        return true;
    }

    @Override
    public Boolean remove(RemoveCategoryDTO dto) {
        rateLimiter.applyFor("Category:Remove", MINIMUM_TPS);

        Category category = categoryRepository.byId(dto.getCategoryId());
        category.onDelete(dto.getIsDeleteFile());
        categoryRepository.delete(category);

        return true;
    }
}
