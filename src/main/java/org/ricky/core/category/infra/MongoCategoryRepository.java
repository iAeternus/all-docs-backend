package org.ricky.core.category.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.category.domain.Category;
import org.ricky.core.category.domain.CategoryRepository;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.core.common.util.RegexUtil.fuzzySearchKeyword;
import static org.ricky.core.common.util.ValidationUtil.isBlank;
import static org.ricky.core.common.util.ValidationUtil.requireNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/19
 * @className MongoCategoryRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoCategoryRepository extends MongoBaseRepository<Category> implements CategoryRepository {

    @Override
    public List<Category> listByName(String name) {
        Query query = query(where("name").is(name));
        return mongoTemplate.find(query, Category.class);
    }

    @Override
    public void save(Category category) {
        super.save(category);
    }

    @Override
    public Category byId(String id) {
        return super.byId(id);
    }

    @Override
    public Optional<Category> byIdOptional(String id) {
        if (isBlank(id)) {
            return Optional.empty();
        }
        return super.byIdOptional(id);
    }

    @Override
    public boolean exists(String arId) {
        return super.exists(arId);
    }

    @Override
    public void delete(Category category) {
        super.delete(category);
    }

    @Override
    public Set<String> fuzzyByKeyword(String keyword) {
        requireNotBlank(keyword, "Keyword must not be blank.");

        Pattern pattern = fuzzySearchKeyword(keyword);
        Query query = query(where("name").regex(pattern));
        List<Category> categories = mongoTemplate.find(query, Category.class);

        return categories.stream()
                .filter(Category::isNotEmpty)
                .map(Category::getId)
                .collect(toImmutableSet());
    }
}
