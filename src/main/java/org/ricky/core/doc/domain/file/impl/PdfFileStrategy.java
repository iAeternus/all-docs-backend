package org.ricky.core.doc.domain.file.impl;

import org.ricky.core.doc.domain.file.FileStrategy;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;

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

    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {

    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) {

    }
}
