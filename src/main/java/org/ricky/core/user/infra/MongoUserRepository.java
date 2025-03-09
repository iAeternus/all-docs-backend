package org.ricky.core.user.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.ofNullable;
import static org.ricky.common.constants.ConfigConstant.USER_COLLECTION;
import static org.ricky.common.util.ValidationUtil.requireNonBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/5
 * @className MongoUserRepository
 * @desc
 */
@Repository
@RequiredArgsConstructor
public class MongoUserRepository extends MongoBaseRepository<User> implements UserRepository {

    private final MongoCachedUserRepository cachedUserRepository;

    @Override
    public User cachedById(String id) {
        requireNonBlank(id, "User ID must not be blank.");
        return cachedUserRepository.cachedById(id);
    }

    @Override
    public List<User> listByUsername(String username) {
        Query query = new Query().addCriteria(where("username").is(username));
        return mongoTemplate.find(query, User.class, USER_COLLECTION);
    }

    @Override
    public void save(User user) {
        super.save(user);
        cachedUserRepository.evictUserCache(user.getId());
    }

    @Override
    public Optional<User> getByUsernameAndPasswordOptional(String username) {
        requireNonBlank(username, "Username must not be blank");

        Criteria criteria = new Criteria("username").is(username);
        return ofNullable(mongoTemplate.findOne(query(criteria), User.class));
    }

    @Override
    public void updateLastLogin(String userId) {
        Query query = new Query(where("_id").is(userId));
        Update update = new Update().set("lastLogin", LocalDateTime.now());
        mongoTemplate.updateFirst(query, update, User.class, USER_COLLECTION);
        cachedUserRepository.evictUserCache(userId);
    }

    @Override
    public void deleteById(User user) {
        super.delete(user);
        cachedUserRepository.evictUserCache(user.getId());
    }

    @Override
    public boolean exists(String arId) {
        return super.exists(arId);
    }

    @Override
    public List<User> listByIds(Set<String> ids) {
        return byIdsAll(ids);
    }

    @Override
    public void delete(List<User> users) {
        super.delete(users);
        cachedUserRepository.evictAll();
    }
}
