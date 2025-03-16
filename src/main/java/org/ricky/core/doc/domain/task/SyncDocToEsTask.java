package org.ricky.core.doc.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.task.RepeatableTask;
import org.ricky.common.exception.MyException;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.ricky.core.doc.domain.DocSearchRepository;
import org.ricky.core.doc.domain.es.EsFile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.ricky.common.exception.ErrorCodeEnum.*;
import static org.ricky.common.util.FileUtil.deleteFile;
import static org.ricky.common.util.FileUtil.isNotExists;
import static org.ricky.core.doc.domain.DocTypeEnum.TXT;
import static org.ricky.core.doc.domain.task.DocTaskContext.buildPath;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className SyncDocToEsTask
 * @desc 文本索引到es中
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncDocToEsTask implements RepeatableTask {

    private final DocRepository docRepository;
    private final DocSearchRepository docSearchRepository;

    public void run(DocTaskContext context) {
        Doc doc = context.getDoc();
        byte[] buf = docRepository.getFileBytes(doc.getGridFsId());
        InputStream docInputStream = new ByteArrayInputStream(buf);

        try {
            String txtFilePath = uploadFileToEs(docInputStream, doc, context);
            doc.setDesc(txtFilePath);
            uploadFileToGridFs(doc, txtFilePath);
            deleteFile(txtFilePath);
        } catch (Exception e) {
            log.error("建立es索引出错", e);
            throw new MyException(BUILD_ES_INDEX_FAILED, "建立es索引出错");
        }
    }

    private String uploadFileToEs(InputStream is, Doc doc, DocTaskContext context) {
        try {
            String textFilePath = buildPath(doc.getName() + doc.getMd5(), ".txt");
            context.setTxtFilePath(textFilePath);

            doc.readText(is, textFilePath);
            if (isNotExists(textFilePath)) {
                throw new MyException(FILE_NOT_FOUND, "文本文件不存在，需要进行重新提取", Map.of("textFilePath", textFilePath));
            }

            EsFile esFile = new EsFile(doc.getMd5(), doc.getName(), doc.getContentType());
            esFile.readFile(textFilePath);
            docSearchRepository.upload(esFile);
            return textFilePath;
        } catch (IOException e) {
            log.error("存入es的过程中出错", e);
            throw new MyException(STORE_TO_ES_FAILED, "存入es的过程中出错");
        }
    }

    private void uploadFileToGridFs(Doc doc, String txtFilePath) {
        try (FileInputStream is = new FileInputStream(txtFilePath)) {
            String txtObjId = docRepository.upload(doc.getId(), TXT.getFilePrefix(), is, TXT.getContentType());
            doc.setTextFileId(txtObjId);
        } catch (IOException e) {
            log.error("存储文本文件出错，请核对", e);
            throw new MyException(STORE_TXT_FAILED, "存储文本文件出错，请核对");
        }
    }

}
