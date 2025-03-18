package org.ricky.core.doc.domain.file;

import lombok.extern.slf4j.Slf4j;
import org.ricky.common.lock.LockedMethod;
import org.ricky.core.doc.domain.FileTypeEnum;
import org.ricky.core.doc.domain.file.impl.*;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.ricky.core.common.util.ValidationUtil.isNull;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className FileStrategyFactory
 * @desc
 */
@Slf4j
public class FileStrategyFactory implements FileStrategy {

    private final Map<FileTypeEnum, FileStrategy> fileStrategies = new ConcurrentHashMap<>();
    private final ThreadLocal<FileTypeEnum> docTypeThreadLocal = new ThreadLocal<>();
    private static volatile FileStrategyFactory instance;

    private FileStrategyFactory() {
    }

    public static FileStrategyFactory getInstance() {
        if (isNull(instance)) {
            synchronized (FileStrategyFactory.class) {
                if (isNull(instance)) {
                    instance = new FileStrategyFactory();
                }
            }
        }
        return instance;
    }

    @LockedMethod(key = "set_doc_type")
    public void setDocType(FileTypeEnum docType) {
        docTypeThreadLocal.set(docType);
        log.debug("DocType set to: {}", docType);
    }

    @Override
    public void readText(InputStream is, String textFilePath) throws IOException {
        executeWithDocType(docType -> {
            FileStrategy strategy = getStrategy(docType);
            strategy.readText(is, textFilePath);
        });
    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {
        executeWithDocType(docType -> {
            FileStrategy strategy = getStrategy(docType);
            strategy.makeThumb(is, picPath);
        });
    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) throws IOException {
        executeWithDocType(docType -> {
            FileStrategy strategy = getStrategy(docType);
            strategy.makePreviewFile(is, context);
        });
    }

    public FileTypeEnum getDocType() {
        return docTypeThreadLocal.get();
    }

    public void removeDocType() {
        docTypeThreadLocal.remove();
        log.debug("DocType removed");
    }

    private void executeWithDocType(DocTypeConsumer consumer) throws IOException {
        FileTypeEnum docType = docTypeThreadLocal.get();
        log.debug("Current DocType: {}", docType);

        if (docType == null) {
            throw new IllegalStateException("DocType is not set");
        }

        consumer.accept(docType);
    }

    private FileStrategy getStrategy(FileTypeEnum docType) {
        return fileStrategies.computeIfAbsent(docType, this::createStrategy);
    }

    @LockedMethod(key = "create_strategy")
    private FileStrategy createStrategy(FileTypeEnum docType) {
        return switch (docType) {
            case PDF -> new PdfFileStrategy();
            case DOCX, XLSX -> new DocxFileStrategy();
            case PPTX -> new PptxFileStrategy();
            case MD, HTML, TXT -> new TxtFileStrategy();
            case JPG, JPEG, PNG -> new PicFileStrategy();
            default -> throw new IllegalArgumentException("Unsupported DocType: " + docType);
        };
    }

    @FunctionalInterface
    private interface DocTypeConsumer {
        void accept(FileTypeEnum docType) throws IOException;
    }
}