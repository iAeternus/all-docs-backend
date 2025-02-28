package org.ricky.common.converter.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.String.format;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className FileConverter
 * @desc 文件转换器
 */
@Slf4j
public abstract class FileConverter {

    /**
     * 转换任务开始的时间
     */
    private long startTime;

    /**
     * 处理阶段开始的时间
     */
    private long startOfProcessTime;

    /**
     * 输入流
     */
    protected final InputStream inStream;

    /**
     * 输出流
     */
    protected final OutputStream outStream;

    /**
     * 是否输出转换过程中的消息
     */
    protected final boolean showOutputMessages;

    /**
     * 是否在转换完成后关闭输入流和输出流
     */
    protected final boolean closeStreamsWhenComplete;

    public FileConverter(InputStream inStream, OutputStream outStream) {
        this(inStream, outStream, false, true);
    }

    public FileConverter(InputStream inStream, OutputStream outStream, boolean showOutputMessages, boolean closeStreamsWhenComplete) {
        this.inStream = inStream;
        this.outStream = outStream;
        this.showOutputMessages = showOutputMessages;
        this.closeStreamsWhenComplete = closeStreamsWhenComplete;
    }

    /**
     * 具体的转换逻辑
     */
    public abstract void convert() throws Exception;

    /**
     * 记录并打印加载信息
     */
    protected void loading() {
        sendToOutputOrNot("\nLoading stream\n\n");
        startTime();
    }

    /**
     * 记录并打印处理阶段信息
     */
    protected void processing() {
        long currentTime = System.currentTimeMillis();
        long prevProcessTook = currentTime - startOfProcessTime;

        sendToOutputOrNot(format("Load completed in %1$dms, now converting...\n\n", prevProcessTook));

        this.startOfProcessTime = System.currentTimeMillis();
    }

    /**
     * 记录并输出保存阶段的消息
     */
    protected void finished() {
        long currentTime = System.currentTimeMillis();
        long timeTaken = currentTime - startTime;
        long prevProcessTook = currentTime - startOfProcessTime;

        this.startOfProcessTime = System.currentTimeMillis();

        if (closeStreamsWhenComplete) {
            try {
                inStream.close();
                outStream.close();
            } catch (IOException ex) {
                // Nothing done
            }
        }

        sendToOutputOrNot(format("Conversion took %1$dms.\n\nTotal: %2$dms\n", prevProcessTook, timeTaken));
    }

    private void startTime() {
        this.startTime = System.currentTimeMillis();
        this.startOfProcessTime = startTime;
    }

    private void sendToOutputOrNot(String toBePrinted) {
        if (showOutputMessages) {
            actuallySendToOutput(toBePrinted);
        }
    }

    protected void actuallySendToOutput(String toBePrinted) {
        log.info(toBePrinted);
    }

}
