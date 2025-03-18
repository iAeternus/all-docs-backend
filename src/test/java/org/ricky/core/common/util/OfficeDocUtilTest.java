package org.ricky.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.ricky.core.common.util.OfficeDocUtil.parseToText;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className OfficeDocUtilTest
 * @desc
 */
@Slf4j
class OfficeDocUtilTest {

    @Test
    void should_read_docx_to_text() throws TikaException, IOException, SAXException {
        // Given
        String filePath = "src/test/resources/答辩稿.docx";
        FileInputStream fileInputStream = new FileInputStream(filePath);

        // When
        String content = parseToText(fileInputStream);

        // Then
        assertFalse(content.isEmpty());
        log.info(content);
    }

}