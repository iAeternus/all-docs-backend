package org.ricky.core.user.infra;

import lombok.RequiredArgsConstructor;
import org.ricky.common.exception.MyException;
import org.ricky.common.mongo.MongoBaseRepository;
import org.ricky.core.common.domain.page.Pagination;
import org.ricky.core.user.domain.User;
import org.ricky.core.user.domain.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.ricky.common.constants.ConfigConstant.USER_COLLECTION;
import static org.ricky.common.exception.ErrorCodeEnum.AR_NOT_FOUND;
import static org.ricky.common.exception.ErrorCodeEnum.FAILURE_TO_UPLOAD_DFS;
import static org.ricky.core.common.domain.page.Pagination.pagination;
import static org.ricky.core.common.util.ValidationUtil.*;
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
    private final GridFsUserRepository gridFsUserRepository;

    @Override
    public User cachedById(String id) {
        requireNotBlank(id, "User ID must not be blank.");
        return cachedUserRepository.cachedById(id);
    }

    @Override
    public List<User> listByUsername(String username) {
        Query query = query(where("username").is(username));
        return mongoTemplate.find(query, User.class, USER_COLLECTION);
    }

    @Override
    public void save(User user) {
        super.save(user);
        cachedUserRepository.evictUserCache(user.getId());
    }

    @Override
    public Optional<User> getByUsernameAndPasswordOptional(String username) {
        requireNotBlank(username, "Username must not be blank");

        Query query = query(where("username").is(username));
        return ofNullable(mongoTemplate.findOne(query, User.class));
    }

    @Override
    public void updateLastLogin(String userId) {
        Query query = query(where("_id").is(userId));
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

    @Override
    public long count() {
        long cnt = super.count();
        if (cnt < 1) {
            throw new MyException(AR_NOT_FOUND, "User not found.");
        }
        return cnt;
    }

    @Override
    public List<User> page(int pageIndex, int pageSize) {
        requireTrue(pageIndex > 0, "PageNum must be greater than 0.");
        requireTrue(pageSize > 0, "PageSize must be greater than 0.");

        Pagination pagination = pagination(pageIndex, pageSize);

        Query query = new Query()
                .skip(pagination.skip())
                .limit(pagination.limit())
                .with(Sort.by(Sort.Direction.DESC, "updatedAt"));
        List<User> users = mongoTemplate.find(query, User.class);
        return isEmpty(users) ? List.of() : users;
    }

    @Override
    public String uploadAvatar(String userId, MultipartFile img) {
        try {
            String gridFsId = gridFsUserRepository.upload("userAvatar", img.getInputStream(), img.getContentType());
            cachedUserRepository.evictUserCache(userId);
            return gridFsId;
        } catch (IOException ex) {
            throw new MyException(FAILURE_TO_UPLOAD_DFS, "上传dfs失败", Map.of("msg", ex.getLocalizedMessage()));
        }
    }

    @Override
    public void deleteAvatar(String userId, String avatar) {
        gridFsUserRepository.delete(avatar);
        cachedUserRepository.evictUserCache(userId);
    }

    // 由于avatars可能分属不同user，故删除所有缓存
    @Override
    public void deleteAvatars(Set<String> avatars) {
        gridFsUserRepository.delete(avatars);
        cachedUserRepository.evictAll();
    }

}
