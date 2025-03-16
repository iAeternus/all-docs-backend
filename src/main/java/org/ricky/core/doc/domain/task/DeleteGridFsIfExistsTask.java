package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.util.ValidationUtil;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DeleteGridFsIfExistsTask
 * @desc 删除gridFs中残留的doc关联文件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteGridFsIfExistsTask implements RepeatableTask {

    private final DocRepository docRepository;

    public void run(String textFileId, String previewFileId, String thumbId) {
        Set<String> ids = Stream.of(textFileId, previewFileId, thumbId)
                .filter(ValidationUtil::isNotBlank)
                .collect(toImmutableSet());
        docRepository.deleteGridFs(ids);
    }

}
