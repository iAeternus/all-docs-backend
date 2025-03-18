package org.ricky.core.doc.domain.file.impl;

import org.ricky.core.doc.domain.file.FileStrategy;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;

import static org.ricky.core.common.util.PdfUtil.extractTextToFile;
import static org.ricky.core.common.util.PdfUtil.generateThumbnail;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className PdfFileStrategy
 * @desc
 */
public class PdfFileStrategy implements FileStrategy {
    @Override
    public void readText(InputStream is, String textFilePath) throws IOException {
        extractTextToFile(is, textFilePath);
    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {
        generateThumbnail(is, picPath, 2.0f);
    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) {
        // no actions
    }
}
