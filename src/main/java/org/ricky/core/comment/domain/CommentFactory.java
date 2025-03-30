package org.ricky.core.comment.domain;

import org.springframework.stereotype.Component;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentFactory
 * @desc
 */
@Component
public class CommentFactory {

    public Comment create(String docId, String content) {
        return new Comment(docId, content);
    }

}
