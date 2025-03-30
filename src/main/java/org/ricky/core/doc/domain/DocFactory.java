package org.ricky.core.doc.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.properties.SystemProperties;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryFactory;
import org.ricky.core.category.domain.CategoryRepository;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;
import org.ricky.core.doc.domain.es.EsPage;
import org.ricky.core.doc.domain.vo.DocVO;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.tag.domain.TagFactory;
import org.ricky.core.tag.domain.TagRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocFactory
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocFactory {

    private final SystemProperties systemProperties;
    private final CategoryFactory categoryFactory;
    private final TagFactory tagFactory;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public CreateDocResult upload(MultipartFile file, String md5, String desc) {
        String name = handlePath(file.getOriginalFilename());
        Boolean adminReview = systemProperties.getAdminReview();
        Doc doc = new Doc(name, file.getSize(), md5, file.getContentType(), desc, adminReview);
        CommentHierarchy commentHierarchy = new CommentHierarchy(doc);
        return CreateDocResult.builder()
                .doc(doc)
                .commentHierarchy(commentHierarchy)
                .build();
    }

    private String handlePath(String originalFilename) {
        String path = originalFilename.replace("\\", "/");
        if (!path.contains("/")) {
            return path;
        }

        String[] split = path.split("/");
        return split[split.length - 1];
    }

    public DocVO doc2docVO(Doc doc) {
        return doc2docVO(doc, null);
    }

    public DocVO doc2docVO(Doc doc, List<EsPage> esPages) {
        Category category = categoryRepository.byIdOptional(doc.getCategoryId()).orElseGet(() -> {
            log.warn("文档[{}]还未关联分组", doc.getId());
            return null;
        });

        List<Tag> tags = tagRepository.byIds(doc.getTagIds().stream().collect(toImmutableSet()));
        return DocVO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .size(doc.getSize())
                .desc(doc.getDesc())
                .collectCnt(0)
                .commentCnt(0)
                .categoryVO(categoryFactory.category2vo(category))
                .thumbId(doc.getThumbId())
                .txtId(doc.getTxtId())
                .previewFileId(doc.getPreviewFileId())
                .status(doc.getStatus())
                .errorMsg(doc.getErrorMsg())
                .tagVOs(tags.stream()
                        .map(tagFactory::tag2vo)
                        .collect(toImmutableList()))
                .esPages(esPages)
                .uploadAt(doc.getUploadAt())
                .updateAt(doc.getUpdatedAt())
                .updateBy(doc.getUpdatedBy())
                .build();
    }
}
