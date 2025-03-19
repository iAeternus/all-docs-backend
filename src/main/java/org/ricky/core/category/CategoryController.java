package org.ricky.core.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ricky.core.category.domain.dto.CategoryDTO;
import org.ricky.core.category.domain.dto.ConnectDTO;
import org.ricky.core.category.domain.dto.DisConnectDTO;
import org.ricky.core.category.domain.dto.RemoveCategoryDTO;
import org.ricky.core.category.domain.vo.CategoryVO;
import org.ricky.core.category.service.CategoryService;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.common.validation.id.Id;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.ricky.common.constants.ConfigConstant.CATEGORY_ID_PREFIX;
import static org.ricky.core.common.domain.ApiResult.success;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className CategoryController
 * @desc
 */
@Validated
@CrossOrigin
@RestController
@Tag(name = "分类模块")
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "创建分类")
    public ApiResult<String> create(@RequestBody @Valid CategoryDTO dto) {
        return success(categoryService.create(dto));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "根据ID获取分类")
    public ApiResult<CategoryVO> getById(@PathVariable("categoryId") @Id(pre = CATEGORY_ID_PREFIX) String categoryId) {
        return success(categoryService.getById(categoryId));
    }

    @PutMapping("/connect")
    @Operation(summary = "连接文档与分类")
    public ApiResult<Boolean> connect(@RequestBody @Valid ConnectDTO dto) {
        return success(categoryService.connect(dto));
    }

    @PutMapping("/disconnect")
    @Operation(summary = "断开文档与分类")
    public ApiResult<Boolean> disconnect(@RequestBody @Valid DisConnectDTO dto) {
        return success(categoryService.disconnect(dto));
    }

    @DeleteMapping
    @Operation(summary = "删除分类")
    public ApiResult<Boolean> remove(@RequestBody @Valid RemoveCategoryDTO dto) {
        return success(categoryService.remove(dto));
    }

}
