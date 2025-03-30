package org.ricky.core.doc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.ricky.core.commenthierarchy.domain.CommentHierarchy;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CreateDocResult
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CreateDocResult {

    Doc doc;
    CommentHierarchy commentHierarchy;

}
