package org.ricky.core.tag.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.category.domain.Category;
import org.ricky.core.tag.domain.Tag;
import org.ricky.core.tag.domain.TagRepository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.core.common.util.RegexUtil.fuzzySearchKeyword;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className MongoTagRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoTagRepository extends MongoBaseRepository<Tag> implements TagRepository {

    private final MongoCachedTagRepository cachedTagRepository;

    @Override
    public Optional<Tag> byNameOptional(String name) {
        requireNotBlank(name, "Tag name must not be blank");

        Query query = query(where("name").is(name));
        Tag tag = mongoTemplate.findOne(query, Tag.class);
        return Optional.ofNullable(tag);
    }

    @Override
    public void save(Tag tag) {
        super.save(tag);
        cachedTagRepository.evictTagCache(tag.getId());
    }

    @Override
    public Tag byId(String id) {
        return super.byId(id);
    }

    @Override
    public List<Tag> byIds(Set<String> tagIds) {
        return super.byIds(tagIds);
    }

    @Override
    public Set<String> fuzzyByKeyword(String keyword) {
        requireNotBlank(keyword, "Keyword must not be blank.");

        Pattern pattern = fuzzySearchKeyword(keyword);
        Query query = query(where("name").regex(pattern));
        List<Tag> tags = mongoTemplate.find(query, Tag.class);

        return tags.stream()
                .map(Tag::getId)
                .collect(toImmutableSet());
    }


}
