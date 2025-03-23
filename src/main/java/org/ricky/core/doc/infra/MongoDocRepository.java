package org.ricky.core.doc.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.common.domain.page.Pagination;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.core.common.domain.page.Pagination.pagination;
import static org.ricky.core.common.util.RegexUtil.fuzzySearchKeyword;
import static org.ricky.core.common.util.ValidationUtil.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
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
    public void deleteGridFs(String docId, Set<String> filenames) {
        gridFsDocRepository.delete(filenames);
        cachedDocRepository.evictDocCache(docId);
    }

    @Override
    public void deleteGridFss(Set<String> filenames) {
        gridFsDocRepository.delete(filenames);
        cachedDocRepository.evictAll();
    }

    @Override
    public byte[] getFileBytes(String gridFsId) {
        return gridFsDocRepository.getFileBytes(gridFsId);
    }

    @Override
    public void delete(Doc doc) {
        super.delete(doc);
        cachedDocRepository.evictDocCache(doc.getId());
    }

    @Override
    public void delete(List<Doc> docs) {
        super.delete(docs);
        cachedDocRepository.evictAll();
    }

    @Override
    public boolean exists(String docId) {
        return super.exists(docId);
    }

    @Override
    public List<Doc> listByCategoryId(String categoryId) {
        requireNotBlank(categoryId, "Category ID must not be blank.");

        Query query = query(where("categoryId").is(categoryId));
        return mongoTemplate.find(query, Doc.class);
    }

    @Override
    public List<Doc> page(Set<String> docIds, Set<String> categoryIds, Set<String> tagIds, int pageIndex, int pageSize) {
        requireTrue(pageIndex > 0, "PageNum must be greater than 0.");
        requireTrue(pageSize > 0, "PageSize must be greater than 0.");

        Pagination pagination = pagination(pageIndex, pageSize);

        Query query = query(new Criteria().orOperator(where("_id").in(docIds), where("categoryId").in(categoryIds), where("tagIds").all(tagIds)))
                .skip(pagination.skip())
                .limit(pagination.limit())
                .with(Sort.by(DESC, "updatedAt"));

        List<Doc> docs = mongoTemplate.find(query, Doc.class);
        return isEmpty(docs) ? List.of() : docs;
    }

    @Override
    public Set<String> fuzzyByKeyword(String keyword) {
        requireNotBlank(keyword, "Keyword must not be blank.");

        // 标题模糊查询 TODO 调用es查询
        Pattern pattern = fuzzySearchKeyword(keyword);
        Query query = query(where("name").regex(pattern));
        List<Doc> docs = mongoTemplate.find(query, Doc.class);

        return docs.stream()
                .map(Doc::getId)
                .collect(toImmutableSet());
    }
}
