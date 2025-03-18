package org.ricky.core.doc.domain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static org.ricky.core.common.util.ValidationUtil.isNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className FileTypeEnum
 * @desc 文件类型枚举
 */
@Getter
public enum FileTypeEnum {

    // PDF 类的文档
    PDF("pdf", "doc_", "application/pdf"),

    // Office 类的文档
    DOC("doc", "doc_", "application/msword"),
    DOCX("docx", "doc_", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    PPT("ppt", "ppt_", "application/vnd.ms-powerpoint"),
    PPTX("pptx", "ppt_", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    XLS("xls", "xls_", "application/vnd.ms-excel"),
    XLSX("xlsx", "xls_", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),

    // 文本类的文档
    HTML("html", "txt_", "text/html"),
    MD("markdown", "txt_", "text/markdown"),
    TXT("txt", "txt_", "text/plain"),

    // 图片类的文档
    JPG("jpg", "img_", "image/jpeg"),
    JPEG("jpeg", "img_", "image/jpeg"),
    PNG("png", "img_", "image/png"),

    // unknown
    UNKNOWN("unknown", "unknown_", "application/octet-stream");

    final String desc;
    final String filePrefix;
    final String contentType;

    FileTypeEnum(String desc, String filePrefix, String contentType) {
        this.desc = desc;
        this.filePrefix = filePrefix;
        this.contentType = contentType;
    }

    public static FileTypeEnum getDocType(String suffixName) {
        if (isNotBlank(suffixName)) {
            suffixName = StringUtils.toRootLowerCase(suffixName);
        }
        return switch (suffixName) {
            case ".pdf" -> PDF;
            case ".doc" -> DOC;
            case ".docx" -> DOCX;
            case ".ppt" -> PPT;
            case ".pptx" -> PPTX;
            case ".xls" -> XLS;
            case ".xlsx" -> XLSX;
            case ".md" -> MD;
            case ".html", ".xhtml", ".xht", ".htm" -> HTML;
            case ".txt" -> TXT;
            case ".jpeg" -> JPEG;
            case ".jpg" -> JPG;
            case ".png" -> PNG;
            default -> UNKNOWN;
        };
    }
}