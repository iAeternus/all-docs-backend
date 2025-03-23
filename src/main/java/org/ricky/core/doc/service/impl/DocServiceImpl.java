package org.ricky.core.doc.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.common.properties.SystemProperties;
import org.ricky.common.ratelimit.RateLimiter;
import org.ricky.common.sensitiveword.service.SensitiveWordService;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.comment.domain.CommentRepository;
import org.ricky.core.common.domain.page.PageVO;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocDomainService;
import org.ricky.core.doc.domain.DocFactory;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.dto.DocPageDTO;
import org.ricky.core.doc.domain.dto.RemoveDocDTO;
import org.ricky.core.doc.domain.dto.UploadDocDTO;
import org.ricky.core.doc.domain.vo.DocVO;
import org.ricky.core.doc.service.DocService;
import org.ricky.core.tag.domain.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.exception.ErrorCodeEnum.HAS_SENSITIVE_WORD;
import static org.ricky.common.exception.MyException.accessDeniedException;
import static org.ricky.common.ratelimit.TPSConstants.*;
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
    private final SensitiveWordService sensitiveWordService;
    private final SystemProperties systemProperties;
    private final DocDomainService docDomainService;
    private final DocFactory docFactory;
    private final DocRepository docRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

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
        return docFactory.doc2docVO(doc);
    }

    @Override
    public PageVO<DocVO> page(DocPageDTO dto) {
        rateLimiter.applyFor("Doc:Page", NORMAL_TPS);

        if (sensitiveWordService.hasSensitiveWord(dto.getKeyword())) {
            throw new MyException(HAS_SENSITIVE_WORD, "这个人输入了非法字符，不知道他到底要查什么~", "filterWord", dto.getKeyword());
        }

        Set<String> categoryIds = categoryRepository.fuzzyByKeyword(dto.getKeyword());
        Set<String> tagIds = tagRepository.fuzzyByKeyword(dto.getKeyword());
        Set<String> docIds = Stream.concat(
                commentRepository.fuzzyByKeyword(dto.getKeyword()).stream(),
                docRepository.fuzzyByKeyword(dto.getKeyword()).stream()
        ).collect(toImmutableSet());

        Optional.ofNullable(dto.getCategoryId()).ifPresent(categoryIds::add);
        Optional.ofNullable(dto.getTagId()).ifPresent(tagIds::add);

        List<Doc> docs = docRepository.page(docIds, categoryIds, tagIds, dto.getPageIndex(), dto.getPageSize());

        List<DocVO> docVOS = docs.stream()
                .map(docFactory::doc2docVO)
                .collect(toImmutableList());

        return PageVO.<DocVO>builder()
                .totalCnt(docVOS.size())
                .pageIndex(dto.getPageIndex())
                .pageSize(dto.getPageSize())
                .data(docVOS)
                .build();
    }

}
