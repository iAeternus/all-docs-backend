package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.ricky.common.exception.ErrorCodeEnum.STORE_THUMB_FAILED;
import static org.ricky.common.exception.ErrorCodeEnum.UPDATE_FILE_THUMB_FAILED;
import static org.ricky.core.common.util.FileUtil.*;
import static org.ricky.core.common.util.UUIDGenerator.newShortUUID;
import static org.ricky.core.doc.domain.FileTypeEnum.PNG;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className UpdateFileThumbTask
 * @desc 制作不同分辨率的缩略图
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateFileThumbTask implements RepeatableTask {

    private final DocRepository docRepository;

    public void run(DocTaskContext context) {
        byte[] buf = docRepository.getFileBytes(context.getDoc().getGridFsId());
        InputStream docInputStream = new ByteArrayInputStream(buf);

        try {
            updateFileThumb(docInputStream, context.getDoc(), context);
        } catch (Exception e) {
            log.error("建立缩略图的时候出错啦！", e);
            throw new MyException(UPDATE_FILE_THUMB_FAILED, "建立缩略图的时候出错啦！");
        }
    }

    private void updateFileThumb(InputStream is, Doc doc, DocTaskContext context) throws IOException {
        String picPath = buildPath(newShortUUID(), ".png");
        context.setThumbFilePath(picPath);

        doc.makeThumb(is, picPath);
        if (isNotExists(picPath)) {
            return;
        }

        try (FileInputStream thumbIns = new FileInputStream(picPath)) {
            String thumbId = docRepository.upload(doc.getId(), PNG.getFilePrefix(), thumbIns, PNG.getContentType());
            doc.setThumbId(thumbId);
        } catch (IOException e) {
            log.error("存储缩略图文件报错了，请核对", e);
            throw new MyException(STORE_THUMB_FAILED, "存储缩略图文件报错了，请核对");
        }

        deleteFile(picPath);
    }

}
