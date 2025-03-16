package org.ricky.core.doc.domain.file;

import org.ricky.core.doc.domain.task.DocTaskContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className FileStrategy
 * @desc
 */
public interface FileStrategy {

    /**
     * 读取文件流，取到其中的文字信息
     *
     * @param is           文件流
     * @param textFilePath 存储文本文件
     * @throws IOException -> IO
     */
    void readText(InputStream is, String textFilePath) throws IOException;

    /**
     * 制作缩略图
     *
     * @param is      文件流
     * @param picPath 图片地址
     * @throws IOException -> IOException
     */
    void makeThumb(InputStream is, String picPath) throws IOException;

    /**
     * 制作预览文件
     *
     * @param is      文件流
     * @param context 文档上下文
     */
    void makePreviewFile(InputStream is, DocTaskContext context) throws IOException;

}
