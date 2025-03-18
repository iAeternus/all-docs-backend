package org.ricky.core.doc.domain.file.impl;

import org.ricky.core.doc.domain.file.FileStrategy;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;

import static org.ricky.core.common.util.OfficeDocUtil.extractTextToFile;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className DocxFileStrategy
 * @desc
 */
public class DocxFileStrategy implements FileStrategy {
    @Override
    public void readText(InputStream is, String textFilePath) throws IOException {
        extractTextToFile(is, textFilePath);
    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {
        // no actions
    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) {
        // no actions
    }
}
