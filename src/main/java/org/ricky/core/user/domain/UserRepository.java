package org.ricky.core.user.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserRepository
 * @desc
 */
public interface UserRepository {
    User cachedById(String id);

    List<User> listByUsername(String username);

    void save(User user);

    Optional<User> getByUsernameAndPasswordOptional(String username);

    void updateLastLogin(String userId);

    void deleteById(User user);

    boolean exists(String userId);

    List<User> listByIds(Set<String> ids);

    void delete(List<User> users);
}
