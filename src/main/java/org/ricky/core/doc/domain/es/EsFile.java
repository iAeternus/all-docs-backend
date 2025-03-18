package org.ricky.core.doc.domain.es;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ricky.core.common.domain.marker.Identified;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.ricky.common.constants.ConfigConstant.DOC_INDEX_NAME;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/16
 * @className EsFile
 * @desc es中落库的文档信息
 */
@Slf4j
@Getter
@Document(indexName = DOC_INDEX_NAME)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsFile implements Identified {

    /**
     * 用于存储文件id，md5码
     */
    @Id
    @Field(type = Keyword)
    private String id;

    /**
     * 文件名
     */
    @Field(type = Text, analyzer = "ik_filename")
    private String name;

    /**
     * 文件的type，pdf，word，or txt
     */
    @Field(type = Keyword)
    private String type;

    /**
     * 文件转化成base64编码后所有的内容。
     */
    @Setter
    @Field(type = Text, analyzer = "ik_content")
    private String content;

    public EsFile(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /**
     * 读文件，并转化为base64编码
     *
     * @param path 文件路径
     */
    public void readFile(String path) {
        // 读文件
        File file = new File(path);
        byte[] bytes = getContent(file);
        // 将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 读文件，设置输入流，并转化为base64编码
     *
     * @param inputStream 输入流
     */
    public void readFile(InputStream inputStream) {
        byte[] bytes = getContent(inputStream);
        // 将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 读文件，获取文件内容
     *
     * @param file 文件
     * @return byte[] -> 文件内容
     */
    private byte[] getContent(File file) {
        byte[] bytesArray = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            if (fileInputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            log.error("读取文件出错", e);
        }
        return bytesArray;
    }

    /**
     * 读文件，设置输入流
     *
     * @param inputStream 输入流
     * @return byte[] -> 文件内容
     */
    private byte[] getContent(InputStream inputStream) {
        byte[] bytesArray = new byte[]{};
        try {
            if (inputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            log.error("读取文件出错", e);
        }
        return bytesArray;
    }

    @Override
    public String getId() {
        return id;
    }
}
