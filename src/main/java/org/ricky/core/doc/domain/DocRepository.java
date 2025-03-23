package org.ricky.core.doc.domain;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    /**
     * 删除单个文档的附件
     */
    void deleteGridFs(String docId, Set<String> filenames);

    /**
     * 批量删除文档的附件
     */
    void deleteGridFss(Set<String> filenames);

    byte[] getFileBytes(String gridFsId);

    void delete(Doc doc);

    void delete(List<Doc> docs);

    boolean exists(String docId);

    List<Doc> listByCategoryId(String categoryId);

    List<Doc> page(Set<String> docIds, Set<String> categoryIds, Set<String> tagIds, int pageIndex, int pageSize);

    Set<String> fuzzyByKeyword(String keyword);
}
