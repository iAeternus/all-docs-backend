package org.ricky.core.doc.domain.file.impl;

import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.InputStream;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className PptxFileStrategy
 * @desc
 */
public class PptxFileStrategy extends DocxFileStrategy {

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) {
        super.makePreviewFile(is, context);
    }

}
