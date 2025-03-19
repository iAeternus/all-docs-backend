package org.ricky.core.category.service;

import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.category.domain.dto.DisConnectDTO;
import org.ricky.core.category.domain.dto.RemoveCategoryDTO;
import org.ricky.core.category.domain.vo.CategoryVO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryService
 * @desc
 */
public interface CategoryService {
    String create(CategoryDTO dto);

    CategoryVO getById(String categoryId);

    Boolean connect(ConnectDTO dto);

    Boolean disconnect(DisConnectDTO dto);

    Boolean remove(RemoveCategoryDTO dto);
}
