package org.ricky.core.doc.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.ricky.common.exception.MyException;
import org.ricky.core.category.domain.event.CategoryDisconnectedEvent;
import org.ricky.core.common.domain.AggregateRoot;
import org.ricky.core.doc.domain.event.DocCreatedEvent;
import org.ricky.core.doc.domain.event.DocDeletedEvent;
import org.ricky.core.doc.domain.event.DocSearchedEvent;
import org.ricky.core.doc.domain.file.FileStrategy;
import org.ricky.core.doc.domain.file.FileStrategyFactory;
import org.ricky.core.doc.domain.task.DocTaskContext;
import org.ricky.core.doc.domain.thumbnail.Thumbnail;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ricky.common.constants.ConfigConstant.*;
import static org.ricky.common.exception.ErrorCodeEnum.FILE_READ_FAILED;
import static org.ricky.core.common.domain.OpsLogTypeEnum.CREATE;
import static org.ricky.core.common.domain.OpsLogTypeEnum.UPDATE;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static org.ricky.core.common.util.ValidationUtil.*;
import static org.ricky.core.doc.domain.FileTypeEnum.getDocType;
import static org.ricky.core.doc.domain.ReviewStatusEnum.PASSED;
import static org.ricky.core.doc.domain.ReviewStatusEnum.REVIEWING;

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
public class Doc extends AggregateRoot implements FileStrategy {

    /**
     * 名称
     */
    private String name;

    /**
     * 大小
     */
    private long size;

    /**
     * MD5值
     */
    private String md5;

    /**
     * 类型
     */
    private String contentType;

    /**
     * 描述
     */
    private String desc;

    /**
     * 后缀名
     */
    private String suffix;

    /**
     * 内容
     */
    private byte[] content;

    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 大文件管理GridFS的ID
     */
    private String gridFsId;

    /**
     * 预览图的GridFS的ID
     */
    @Setter
    private String thumbId;

    /**
     * 文本文件的ID
     */
    @Setter
    private String txtId;

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
    @Setter
    private String errorMsg;

    /**
     * 审核状态
     * true 正在审核；false 审核完毕
     */
    private ReviewStatusEnum reviewStatus;

    /**
     * 违禁词列表
     */
    private List<String> sensitiveWords;

    /**
     * 文档标签列表
     */
    private List<String> tagIds;

    /**
     * 上传时间
     */
    private LocalDateTime uploadAt;

    public Doc(String name, long size, String md5, String contentType, String desc, Boolean adminReview) {
        super(newDocId());
        initDoc(name, size, md5, contentType, desc, adminReview);
        addOpsLog(CREATE, "新建");
    }

    public static String newDocId() {
        return DOC_ID_PREFIX + newSnowflakeId();
    }

    public void initDoc(String name, long size, String md5, String contentType, String desc, Boolean adminReview) {
        this.name = name;
        this.size = size;
        this.uploadAt = LocalDateTime.now();
        this.md5 = md5;
        this.contentType = contentType;
        this.desc = desc;
        // 若关闭了审核功能，直接设置状态为审核通过
        this.reviewStatus = isTrue(adminReview) ? REVIEWING : PASSED;
        if (isNotBlank(name)) {
            this.suffix = name.substring(name.lastIndexOf("."));
        }

        this.thumbnailList = new ArrayList<>();
        this.sensitiveWords = new ArrayList<>();
        this.tagIds = new ArrayList<>();
        raiseEvent(new DocCreatedEvent(getId(), suffix, gridFsId, txtId, previewFileId, thumbId));
    }

    public void updateGridFsId(String gripFsId) {
        this.gridFsId = gripFsId;
        addOpsLog("上传到GridFs");
    }

    public void addTag(String tagId) {
        tagIds.add(tagId);
        addOpsLog(UPDATE, "新增标签");
    }

    public boolean containsTag(String tagId) {
        return tagIds.contains(tagId);
    }

    public void updateStatus(DocStatusEnum newStatus) {
        this.status = newStatus;
        addOpsLog(UPDATE, newStatus.getDesc());
    }

    @Override
    public void readText(InputStream is, String textFilePath) throws IOException {
        FileTypeEnum docType = getDocType(suffix);
        try {
            FileStrategyFactory.getInstance().setDocType(docType);
            FileStrategyFactory.getInstance().readText(is, textFilePath);
        } finally {
            FileStrategyFactory.getInstance().removeDocType();
        }
    }

    @Override
    public void makeThumb(InputStream is, String picPath) throws IOException {
        FileTypeEnum docType = getDocType(suffix);
        try {
            FileStrategyFactory.getInstance().setDocType(docType);
            FileStrategyFactory.getInstance().makeThumb(is, picPath);
        } finally {
            FileStrategyFactory.getInstance().removeDocType();
        }
    }

    @Override
    public void makePreviewFile(InputStream is, DocTaskContext context) throws IOException {
        FileTypeEnum docType = getDocType(suffix);
        try {
            FileStrategyFactory.getInstance().setDocType(docType);
            FileStrategyFactory.getInstance().makePreviewFile(is, context);
        } finally {
            FileStrategyFactory.getInstance().removeDocType();
        }
    }

    /**
     * 设置描述内容
     *
     * @param textFilePath 文本文件路径
     */
    public void setDesc(String textFilePath) {
        try {
            List<String> lines = FileUtils.readLines(new File(textFilePath), UTF_8);
            String desc = null;
            if (isNotEmpty(lines)) {
                desc = lines.get(0);
            }

            if (isBlank(desc)) {
                desc = "无描述";
            } else if (desc.length() > MAX_DESC_LENGTH) {
                desc = desc.substring(0, MAX_DESC_LENGTH);
            }

            this.desc = desc;
        } catch (IOException e) {
            throw new MyException(FILE_READ_FAILED, "文件内容读取失败", Map.of("textFilePath", textFilePath));
        }
    }

    public void onDelete() {
        raiseEvent(new DocDeletedEvent(getId(), categoryId));
    }

    public void onSearch() {
        raiseEvent(new DocSearchedEvent(getId()));
    }

    public void connectWithCategory(String categoryId) {
        this.categoryId = categoryId;
        addOpsLog(UPDATE, "关联分类");
    }

    public void disconnectWithCategory() {
        String tmp = this.categoryId;
        this.categoryId = null;
        raiseEvent(new CategoryDisconnectedEvent(tmp, -1));
        addOpsLog(UPDATE, "解除关联分类");
    }

    public boolean hasCategory() {
        return isNotBlank(categoryId);
    }
}
