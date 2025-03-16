package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.tag.domain.TagRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.toRootUpperCase;
import static org.ricky.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className SyncTagToDocTask
 * @desc 同步文档标签
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTagToDocTask implements RepeatableTask {

    private final DocRepository docRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void run(String docId, String suffix) {
        String tagName = toRootUpperCase(suffix.substring(suffix.lastIndexOf(".") + 1));
        if (isBlank(tagName)) {
            return;
        }

        // 查询标签，若不存在则新建
        Tag tag = tagRepository.byNameOptional(tagName).orElseGet(() -> {
            Tag tmp = new Tag(tagName);
            tagRepository.save(tmp);
            return tmp;
        });

        // 文档与标签建立联系
        Doc doc = docRepository.cachedById(docId);
        String tagId = tag.getId();
        if (!doc.containsTag(tagId)) {
            doc.addTag(tagId);
            docRepository.save(doc);
        }
    }

}
