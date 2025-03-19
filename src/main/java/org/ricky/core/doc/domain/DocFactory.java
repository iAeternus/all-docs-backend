package org.ricky.core.doc.domain;

import lombok.RequiredArgsConstructor;
import org.ricky.common.properties.SystemProperties;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryFactory;
import org.ricky.core.doc.domain.es.EsPage;
import org.ricky.core.doc.domain.vo.DocVO;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.tag.domain.TagFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocFactory
 * @desc
 */
@Component
@RequiredArgsConstructor
public class DocFactory {

    private final SystemProperties systemProperties;
    private final CategoryFactory categoryFactory;
    private final TagFactory tagFactory;

    public Doc file2doc(MultipartFile file, String md5, String desc) {
        String name = handlePath(file.getOriginalFilename());
        Boolean adminReview = systemProperties.getAdminReview();
        return new Doc(name, file.getSize(), md5, file.getContentType(), desc, adminReview);
    }

    private String handlePath(String originalFilename) {
        String path = originalFilename.replace("\\", "/");
        if (!path.contains("/")) {
            return path;
        }

        String[] split = path.split("/");
        return split[split.length - 1];
    }

    public DocVO buildDocVO(Doc doc, Integer collectCnt, Integer commentCnt, Category category, List<Tag> tags, List<EsPage> esPages) {
        return DocVO.builder()
                .id(doc.getId())
                .name(doc.getName())
                .size(doc.getSize())
                .desc(doc.getDesc())
                .collectCnt(collectCnt)
                .commentCnt(commentCnt)
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
