package org.ricky.core.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/18
 * @className OfficeDocUtil
 * @desc Microsoft Office 工具
 */
@Slf4j
public class OfficeDocUtil {

    /**
     * 解析 Office 文档（DOCX/XLSX）并写入文本文件（追加模式）
     */
    public static void extractTextToFile(InputStream is, String textPath) {
        try (FileWriter writer = new FileWriter(textPath, true)) {
            String content = parseToText(is);
            writer.write(content);
        } catch (IOException e) {
            log.error("文件写入失败: {}", e.getMessage());
        } catch (TikaException | SAXException e) {
            log.error("文档解析失败: {}", e.getMessage());
        }
    }

    /**
     * 解析 Office 文档（DOCX/XLSX）为纯文本
     */
    public static String parseToText(InputStream is) throws IOException, TikaException, SAXException {
        // 设置无字符数限制
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        OOXMLParser parser = new OOXMLParser(); // 支持 DOCX/XLSX/PPTX

        parser.parse(is, handler, metadata, context);
        return handler.toString();
    }

}
