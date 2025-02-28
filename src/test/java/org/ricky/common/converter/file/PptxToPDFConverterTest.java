package org.ricky.common.converter.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/28
 * @className PptxToPDFConverterTest
 * @desc
 */
class PptxToPDFConverterTest {

    // 中文文件依赖 linux 必须有字体包 https://blog.csdn.net/Darling_qi/article/details/120485688
    static final String inPath = "src/test/resources/2024 Fire Cup Tutorial.pptx";
    static final String outPath = "src/test/resources/2024 Fire Cup Tutorial.pdf";

    @Test
    @EnabledIf("isTestEnabled")
    void should_convert_pptx_to_pdf() throws Exception {
        InputStream inStream = getInFileStream(inPath);
        OutputStream outStream = getOutFileStream(outPath);
        boolean shouldShowMessages = true;
        FileConverter converter = new PptxToPDFConverter(inStream, outStream, shouldShowMessages, true);
        converter.convert();
    }

    protected static InputStream getInFileStream(String inputFilePath) throws FileNotFoundException {
        return new FileInputStream(inputFilePath);
    }

    protected static OutputStream getOutFileStream(String outputFilePath) throws IOException {
        Path outputPath = Paths.get(outputFilePath);
        Files.createDirectories(outputPath.getParent());
        Files.createFile(outputPath);
        return Files.newOutputStream(outputPath, WRITE, CREATE);
    }

    private boolean isTestEnabled() {
        return false;
    }

}