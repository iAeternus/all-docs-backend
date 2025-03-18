package org.ricky.core.doc.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocRepository
 * @desc
 */
public interface DocRepository {
    boolean existsByMd5(String md5);

    String upload(String docId, MultipartFile file) throws IOException;

    String upload(String docId, String prefix, InputStream content, String contentType);

    void save(Doc doc);

    Doc byId(String docId);

    Doc cachedById(String docId);

    void deleteGridFs(String docId, Set<String> filenames);

    byte[] getFileBytes(String gridFsId);

    void delete(Doc doc);

    boolean exists(String docId);
}
