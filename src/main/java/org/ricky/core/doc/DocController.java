package org.ricky.core.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.service.DocService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className DocController
 * @desc
 */
@Slf4j
@Validated
@CrossOrigin
@RestController
@Tag(name = "文档模块")
@RequiredArgsConstructor
@RequestMapping("/doc")
public class DocController {

    private final DocService docService;

    @PostMapping("/upload")
    @Operation(summary = "表单上传文件", description = "当数据库中存在该md5值时，可以实现秒传功能")
    public ApiResult<String> upload(@ModelAttribute UploadDocDTO dto) throws IOException {
        return docService.upload(dto);
    }

}
