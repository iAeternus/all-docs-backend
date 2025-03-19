package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.core.common.util.ValidationUtil.isEmpty;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className SyncDocToCategoryTask
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncDocToCategoryTask implements RepeatableTask {

    private final DocRepository docRepository;

    public void run(String categoryId, Boolean isDeleteFile) {
        List<Doc> docs = docRepository.listByCategoryId(categoryId);
        if (isEmpty(docs)) {
            return;
        }

        docRepository.delete(docs);

        if (!isDeleteFile) {
            return;
        }

        Set<String> gridFsIds = docs.stream()
                .flatMap(doc -> Stream.of(doc.getGridFsId(), doc.getThumbId(), doc.getTxtId()))
                .collect(toImmutableSet());
        docRepository.deleteGridFss(gridFsIds);
    }

}
