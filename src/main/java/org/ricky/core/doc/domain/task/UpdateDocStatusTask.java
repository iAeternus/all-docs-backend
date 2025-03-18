package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.springframework.stereotype.Component;

import static org.ricky.core.doc.domain.DocStatusEnum.ON_PROCESS;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className UpdateDocStatusTask
 * @desc
 */
@Deprecated
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateDocStatusTask implements RepeatableTask {

    public void run(DocTaskContext context) {
        context.getDoc().updateStatus(ON_PROCESS);
    }

}
