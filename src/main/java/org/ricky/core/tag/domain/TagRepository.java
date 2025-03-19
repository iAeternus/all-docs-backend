package org.ricky.core.tag.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    Tag byId(String tagId);

    List<Tag> byIds(Set<String> tagIds);
}
