package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className UpdatePreviewFile
 * @desc 制作预览文件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePreviewFile implements RepeatableTask {

    private final DocRepository docRepository;

    public void run(DocTaskContext context) {
        byte[] buf = docRepository.getFileBytes(context.getDoc().getGridFsId());
        InputStream docInputStream = new ByteArrayInputStream(buf);

        updatePreviewFile(docInputStream, context);
    }

    private void updatePreviewFile(InputStream is, DocTaskContext context) {

    }

}
