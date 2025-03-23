package org.ricky.core.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.common.validation.id.Id;
import org.ricky.core.doc.domain.dto.DocPageDTO;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UpdateDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.vo.DocVO;
import org.ricky.core.doc.service.DocService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.ricky.common.constants.ConfigConstant.DOC_ID_PREFIX;
import static org.ricky.core.common.domain.ApiResult.success;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className DocController
 * @desc
 */
@Validated
@CrossOrigin
@RestController
@Tag(name = "文档模块")
@RequiredArgsConstructor
@RequestMapping("/doc")
public class DocController {

    private final DocService docService;

    @PostMapping("/upload")
    @Operation(summary = "表单上传文档", description = "当数据库中存在该md5值时，可以实现秒传功能")
    public ApiResult<String> upload(@ModelAttribute @Valid UploadDocDTO dto) throws IOException {
        return success(docService.upload(dto));
    }

    @DeleteMapping
    @Operation(summary = "删除文档")
    public ApiResult<Boolean> remove(@RequestBody @Valid RemoveDocDTO dto) {
        return success(docService.remove(dto));
    }

    @PutMapping("/info")
    @Operation(summary = "修改文档基本信息")
    public ApiResult<Boolean> update(@RequestBody @Valid UpdateDocDTO dto) {
        return success(docService.update(dto));
    }


    @GetMapping("/{docId}")
    @Operation(summary = "根据ID查询文档信息")
    public ApiResult<DocVO> getById(@PathVariable("docId") @Id(pre = DOC_ID_PREFIX) String docId) {
        return success(docService.getById(docId));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询文档")
    public ApiResult<PageVO<DocVO>> page(@RequestBody @Valid DocPageDTO dto) {
        return success(docService.page(dto));
    }

}
