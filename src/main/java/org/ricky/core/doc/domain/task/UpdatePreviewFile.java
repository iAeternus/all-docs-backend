package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.ricky.common.exception.ErrorCodeEnum.UPDATE_PREVIEW_FILE_FAILED;

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

        try {
            updatePreviewFile(docInputStream, context.getDoc(), context);
        } catch (Exception e) {
            log.error("建立预览文件的时候出错啦", e);
            throw new MyException(UPDATE_PREVIEW_FILE_FAILED, "建立预览文件的时候出错啦");
        }
    }

    private void updatePreviewFile(InputStream is, Doc doc, DocTaskContext context) throws IOException {
        doc.makePreviewFile(is, context);
    }

}
