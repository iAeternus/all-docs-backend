package org.ricky.core.user.domain;

import java.util.List;

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
}
