package org.ricky.core.tag.domain;

import java.util.Optional;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className TagRepository
 * @desc
 */
public interface TagRepository {
    Optional<Tag> byNameOptional(String name);

    void save(Tag tag);
}
