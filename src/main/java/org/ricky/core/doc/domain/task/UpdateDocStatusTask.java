package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className UpdateDocStatusTask
 * @desc
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDocStatusTask implements RepeatableTask {

    private final DocRepository docRepository;

    public void run(String docId) {
        Doc doc = docRepository.cachedById(docId);
        doc.onProcess();
        docRepository.save(doc);
    }

}
