package org.ricky.core.user.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ricky.common.constants.ConfigConstant.USER_COLLECTION;
import static org.ricky.common.util.ValidationUtil.requireNonBlank;

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
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.find(query, User.class, USER_COLLECTION);
    }

    @Override
    public void save(User user) {
        super.save(user);
        cachedUserRepository.evictAppCache(user.getId());
    }
}
