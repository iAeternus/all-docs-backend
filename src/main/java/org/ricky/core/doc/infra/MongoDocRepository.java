package org.ricky.core.doc.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className MongoDocRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoDocRepository extends MongoBaseRepository<Doc> implements DocRepository {

    private final GridFsDocRepository gridFsDocRepository;
    private final MongoCachedDocRepository cachedDocRepository;

    @Override
    public boolean existsByMd5(String md5) {
        requireNotBlank(md5, "MD5 must not be blank");

        Query query = query(where("md5").is(md5));
        return mongoTemplate.exists(query, Doc.class);
    }

    @Override
    public String upload(String docId, MultipartFile file) throws IOException {
        String gridFsId = gridFsDocRepository.upload(file.getInputStream(), file.getContentType());
        cachedDocRepository.evictDocCache(docId);
        return gridFsId;
    }

    @Override
    public String upload(String docId, String prefix, InputStream content, String contentType) {
        String gridFsId = gridFsDocRepository.upload(prefix, content, contentType);
        cachedDocRepository.evictDocCache(docId);
        return gridFsId;
    }

    @Override
    public void save(Doc doc) {
        super.save(doc);
        cachedDocRepository.evictDocCache(doc.getId());
    }

    @Override
    public Doc byId(String id) {
        return super.byId(id);
    }

    @Override
    public Doc cachedById(String docId) {
        return cachedDocRepository.cachedById(docId);
    }

    @Override
    public void deleteGridFs(Set<String> filenames) {
        gridFsDocRepository.delete(filenames);
        cachedDocRepository.evictAll();
    }

    @Override
    public void deleteGridFs(String... filenames) {
        gridFsDocRepository.delete(filenames);
        cachedDocRepository.evictAll();
    }

    @Override
    public byte[] getFileBytes(String gridFsId) {
        return gridFsDocRepository.getFileBytes(gridFsId);
    }

}
