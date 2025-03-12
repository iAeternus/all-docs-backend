package org.ricky.core.doc.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.domain.AggregateRoot;
import org.ricky.core.doc.domain.thumbnail.Thumbnail;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

import static org.ricky.common.constants.ConfigConstant.DOC_COLLECTION;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className Doc
 * @desc 文档
 */
@Getter
@Document(DOC_COLLECTION)
@TypeAlias(DOC_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Doc extends AggregateRoot {

    /**
     * 名称
     */
    private String name;

    /**
     * 大小
     */
    private long size;

    /**
     * 上传时间
     */
    private LocalDateTime uploadAt;

    /**
     * MD5值
     */
    private String md5;

    /**
     * 内容
     */
    private byte[] content;

    /**
     * 类型
     */
    private String contentType;

    /**
     * 后缀名
     */
    private String suffix;

    /**
     * 描述
     */
    private String description;

    /**
     * 大文件管理GridFS的ID
     */
    private String gridFsId;

    /**
     * 预览图的GridFS的ID
     */
    private String thumbId;

    /**
     * 文本文件的ID
     */
    private String textFileId;

    /**
     * 预览文本文件的ID
     */
    private String previewFileId;

    /**
     * 缩略图
     */
    private List<Thumbnail> thumbnailList;

    /**
     * 状态
     */
    private DocStatusEnum status;

    /**
     * 错误信息
     **/
    private String errorMsg;

    /**
     * 审核状态
     * true 正在审核；false 审核完毕
     */
    private boolean reviewing = true;

    /**
     * 违禁词列表
     */
    private List<String> sensitiveWords;

}
