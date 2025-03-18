package org.ricky.common.mongo;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static cn.hutool.core.io.IoUtil.readBytes;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.exception.ErrorCodeEnum.FILE_READ_FAILED;
import static org.ricky.core.common.util.ValidationUtil.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className GridFsBaseRepository
 * @desc gridFs仓库基类，封装文件基本操作
 */
@Slf4j
public abstract class GridFsBaseRepository {

    protected static final String FILE_NAME = "filename";

    @Autowired
    protected GridFsTemplate gridFsTemplate;

    @Autowired
    protected GridFSBucket gridFSBucket;

    public void delete(String filename) {
        requireNotBlank(filename, "File name must not be blank.");
        gridFsTemplate.delete(query(where(FILE_NAME).is(filename)));
    }

    public void delete(Set<String> filenames) {
        if (isEmpty(filenames)) {
            return;
        }
        gridFsTemplate.delete(query(where(FILE_NAME).in(filenames)));
    }

    public void delete(String... filenames) {
        delete(Stream.of(filenames).collect(toImmutableSet()));
    }

    public String upload(String prefix, InputStream content, String contentType) {
        String filename = prefix + UUIDGenerator.newShortUUID();
        gridFsTemplate.store(content, filename, contentType);
        return filename;
    }

    public String upload(InputStream content, String contentType) {
        return upload("", content, contentType);
    }

    public byte[] getFileBytes(String gridFsId) {
        if (isNotBlank(gridFsId)) {
            Query gridQuery = query(where(FILE_NAME).is(gridFsId));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            if (isNull(fsFile)) {
                return new byte[0];
            }
            try (GridFSDownloadStream in = gridFSBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    return readBytes(resource.getInputStream());
                } else {
                    return new byte[0];
                }
            } catch (IOException ex) {
                throw new MyException(FILE_READ_FAILED, "读取dfs失败", Map.of("gridFsId", gridFsId));
            }
        }
        return new byte[0];
    }

}
