package org.ricky.core.doc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocDomainService;
import org.ricky.core.doc.domain.DocFactory;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.vo.DocVO;
import org.ricky.core.doc.service.DocService;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.tag.domain.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.exception.MyException.accessDeniedException;
import static org.ricky.common.ratelimit.TPSConstants.HIGH_TPS;
import static org.ricky.common.ratelimit.TPSConstants.MINIMUM_TPS;
import static org.ricky.core.common.util.ValidationUtil.isFalse;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocServiceImpl
 * @desc
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {

    private final RateLimiter rateLimiter;
    private final SystemProperties systemProperties;
    private final DocDomainService docDomainService;
    private final DocFactory docFactory;
    private final DocRepository docRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public String upload(UploadDocDTO dto) throws IOException {
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

        return doc.getId();
    }

    @Override
    @Transactional
    public Boolean remove(RemoveDocDTO dto) {
        rateLimiter.applyFor("Doc:Remove", MINIMUM_TPS);

        Doc doc = docRepository.cachedById(dto.getDocId());
        doc.onDelete();
        docRepository.delete(doc);
        if (dto.isDeleteFile()) {
            docRepository.deleteGridFs(doc.getId(), Set.of(doc.getGridFsId(), doc.getThumbId(), doc.getTxtId()));
        }

        return true;
    }

    @Override
    public DocVO getById(String docId) {
        rateLimiter.applyFor("Doc:GetById", HIGH_TPS);

        Doc doc = docRepository.cachedById(docId);
        doc.onSearch();
        Category category = categoryRepository.byIdOptional(doc.getCategoryId()).orElseGet(() -> {
            log.warn("文档还未关联分组");
            return null;
        });
        List<Tag> tags = tagRepository.byIds(doc.getTagIds().stream().collect(toImmutableSet()));

        return docFactory.buildDocVO(doc, 0, 0, category, tags, null);
    }
}
