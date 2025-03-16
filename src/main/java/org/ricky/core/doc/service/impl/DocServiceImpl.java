package org.ricky.core.doc.service.impl;

import lombok.RequiredArgsConstructor;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.core.common.domain.ApiResult;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocDomainService;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.service.DocService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.ricky.common.exception.MyException.accessDeniedException;
import static org.ricky.common.ratelimit.TPSConstants.MINIMUM_TPS;
import static org.ricky.common.util.ValidationUtil.isFalse;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocServiceImpl
 * @desc
 */
@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {

    private final RateLimiter rateLimiter;
    private final SystemProperties systemProperties;
    private final DocDomainService docDomainService;
    private final DocRepository docRepository;

    @Override
    @Transactional
    public ApiResult<String> upload(UploadDocDTO dto) throws IOException {
        rateLimiter.applyFor("Doc:Upload", MINIMUM_TPS);
        if (isFalse(systemProperties.getUserUpload())) {
            throw accessDeniedException();
        }

        // 构建文档
        MultipartFile file = dto.getFile();
        Doc doc = docDomainService.upload(file);

        // 落库
        String gripFsId = docRepository.upload(doc.getId(), file);
        doc.updateGridFsId(gripFsId);
        docRepository.save(doc);

        return ApiResult.success(doc.getId());
    }
}
