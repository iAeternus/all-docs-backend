package org.ricky.core.user.domain;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserRepository
 * @desc
 */
public interface UserRepository {
    User getById(String id);
}
