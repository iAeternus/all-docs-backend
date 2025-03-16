package org.ricky.core.doc.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.ErrorCodeEnum;
import org.ricky.common.exception.MyException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.ricky.common.exception.ErrorCodeEnum.INPUT_STREAM_FAILED;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocInputStream
 * @desc 文档数据流
 */
@Deprecated
@Component
public class DocInputStream {

    @Setter
    private InputStream inputStream;
    private final Lock lock = new ReentrantLock();

    public int read() {
        lock.lock();
        try {
            return inputStream.read();
        } catch (IOException e) {
            throw new MyException(INPUT_STREAM_FAILED, "同步读取文档数据流失败");
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new MyException(INPUT_STREAM_FAILED, "同步关闭文档数据流失败");
        } finally {
            lock.unlock();
        }
    }

}
