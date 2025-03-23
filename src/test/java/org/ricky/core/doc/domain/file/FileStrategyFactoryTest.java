package org.ricky.core.doc.domain.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.ricky.common.exception.MyException;
import org.ricky.core.doc.domain.FileTypeEnum;
import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.ricky.common.exception.ErrorCodeEnum.PROCESS_ERROR;
import static org.ricky.core.doc.domain.FileTypeEnum.*;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className FileStrategyFactoryTest
 * @desc
 */
@Execution(ExecutionMode.CONCURRENT)
class FileStrategyFactoryTest {

    final FileStrategyFactory factory = FileStrategyFactory.getInstance();

    @AfterEach
    void after() {
        factory.removeDocType();
    }

    @Test
    void should_set_doc_type_and_read_text() throws IOException {
        // Given
        factory.setDocType(DOCX);
        InputStream mockInputStream = mock(InputStream.class);

        // When
        factory.readText(mockInputStream, "src/test/resources/testPath");

        // Then
        assertNotNull(factory.getDocType());
    }

    @Test
    void should_make_thumb() throws IOException {
        // Given
        factory.setDocType(JPG);
        InputStream mockInputStream = mock(InputStream.class);

        // When
        factory.makeThumb(mockInputStream, "src/test/resources/testPath");

        // Then
        verify(mockInputStream, never()).close(); // 确保 InputStream 没有被关闭
        assertNotNull(factory.getDocType());
    }

    @Test
    void should_make_preview_file() throws IOException {
        // Given
        factory.setDocType(PDF);
        InputStream mockInputStream = mock(InputStream.class);
        DocTaskContext mockContext = mock(DocTaskContext.class);

        // When
        factory.makePreviewFile(mockInputStream, mockContext);

        // Then
        verify(mockInputStream, never()).close(); // 确保 InputStream 没有被关闭
        assertNotNull(factory.getDocType());
    }

    @Test
    @Disabled
    void should_thread_safety() throws InterruptedException {
        // Given
        AtomicReference<FileTypeEnum> result1 = new AtomicReference<>();
        AtomicReference<FileTypeEnum> result2 = new AtomicReference<>();
        Thread thread1 = new Thread(() -> {
            try {
                factory.setDocType(DOCX);
                Thread.sleep(1000);
                factory.readText(null, "src/test/resources/testPath");
                result1.set(factory.getDocType());
            } catch (IOException e) {
                throw new MyException(PROCESS_ERROR, "Exception in thread1");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                factory.removeDocType();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                factory.setDocType(PDF);
                factory.readText(null, "src/test/resources/testPath2");
                result2.set(factory.getDocType());
            } catch (IOException e) {
                throw new MyException(PROCESS_ERROR, "Exception in thread2");
            } finally {
                factory.removeDocType();
            }
        });

        // When
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Then
        assertEquals(DOCX, result1.get());
        assertEquals(PDF, result2.get());
    }

    @Test
    void should_remove_doc_type() {
        factory.setDocType(TXT);
        assertNotNull(factory.getDocType());

        factory.removeDocType();
        assertNull(factory.getDocType());
    }

    @Test
    void should_fail_to_delegate_if_doc_type_not_support() {
        factory.setDocType(UNKNOWN);
        InputStream mockInputStream = mock(InputStream.class);

        assertThrows(IllegalArgumentException.class, () -> factory.readText(mockInputStream, "src/test/resources/testPath"));
        factory.removeDocType();
    }

}