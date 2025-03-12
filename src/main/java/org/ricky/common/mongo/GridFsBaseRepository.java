package org.ricky.common.mongo;

import com.mongodb.client.gridfs.GridFSBucket;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.exception.MyException;
import org.ricky.common.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.ricky.common.exception.ErrorCodeEnum.FAILURE_TO_UPLOAD_DFS;
import static org.ricky.common.util.ValidationUtil.isEmpty;
import static org.ricky.common.util.ValidationUtil.requireNotBlank;
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

    public String upload(String prefix, InputStream content, String contentType) {
        String filename = prefix + UUIDGenerator.newShortUUID();
        gridFsTemplate.store(content, filename, contentType);
        return filename;
    }

    public String upload(InputStream content, String contentType) {
        return upload("", content, contentType);
    }

}
