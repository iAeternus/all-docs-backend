package org.ricky.core.doc.domain.file.impl;

import org.ricky.core.doc.domain.file.FileStrategy;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className TxtFileStrategy
 * @desc
 */
public class TxtFileStrategy implements FileStrategy {
    @Override
    public void readText(InputStream is, String textFilePath) throws IOException {
        Charset charset = UTF_8;
        Path outputPath = Paths.get(textFilePath);

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
                BufferedWriter writer = Files.newBufferedWriter(outputPath, charset)
        ) {
            char[] buffer = new char[8192];
            int lengthRead;
            while ((lengthRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, lengthRead); // 边读边写，避免内存溢出
            }
        }
    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {
        // no action
    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) {
        // no action
    }
}
