package org.ricky.core.doc.service;

import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;

import java.io.IOException;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocService
 * @desc
 */
public interface DocService {
    ApiResult<String> upload(UploadDocDTO dto) throws IOException;

    ApiResult<Boolean> remove(RemoveDocDTO dto);
}
