package org.ricky.core.commenthierarchy.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.domain.AggregateRoot;
import org.ricky.core.common.domain.idtree.IdTree;
import org.ricky.core.common.domain.idtree.IdTreeHierarchy;
import org.ricky.core.common.domain.idtree.exception.IdNodeLevelOverflowException;
import org.ricky.core.doc.domain.Doc;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Set;

import static org.ricky.common.constants.ConfigConstant.*;
import static org.ricky.common.exception.ErrorCodeEnum.COMMENT_HIERARCHY_TOO_DEEP;
import static org.ricky.common.exception.ErrorCodeEnum.COMMENT_NOT_FOUND;
import static org.ricky.core.common.domain.OpsLogTypeEnum.CREATE;
import static org.ricky.core.common.domain.OpsLogTypeEnum.UPDATE;
import static org.ricky.core.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static org.ricky.core.common.util.ValidationUtil.isNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchy
 * @desc
 */
@Getter
@Document(COMMENT_HIERARCHY_COLLECTION)
@TypeAlias(COMMENT_HIERARCHY_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentHierarchy extends AggregateRoot {

    private String docId;
    private IdTree idTree;
    private IdTreeHierarchy hierarchy;

    public CommentHierarchy(Doc doc) {
        super(newCommentHierarchyId(), doc.getUserId());
        this.docId = doc.getId();
        this.idTree = new IdTree(new ArrayList<>(0));
        buildHierarchy();
        addOpsLog(CREATE, "新建");
    }

    public static String newCommentHierarchyId() {
        return COMMENT_HIERARCHY_ID_PREFIX + newSnowflakeId();
    }

    public void addComment(String parentCommentId, String commentId) {
        if (isNotBlank(parentCommentId)) {
            if (!containsCommentId(parentCommentId)) {
                throw new MyException(COMMENT_NOT_FOUND, "未找到评论", "parentCommentId", parentCommentId);
            }

            if (this.hierarchy.levelOf(parentCommentId) >= MAX_COMMENT_HIERARCHY_LEVEL) {
                throw new MyException(COMMENT_HIERARCHY_TOO_DEEP, "添加失败，评论层级最多不能超过5层", "docId", getDocId());
            }
        }

        idTree.addNode(parentCommentId, commentId);
        buildHierarchy();
        addOpsLog(UPDATE, "添加评论");
    }

    public boolean containsCommentId(String commentId) {
        return hierarchy.containsId(commentId);
    }

    public Set<String> getAllCommentIds() {
        return hierarchy.allIds();
    }

    private void buildHierarchy() {
        try {
            this.hierarchy = this.idTree.buildHierarchy(MAX_COMMENT_HIERARCHY_LEVEL);
        } catch (IdNodeLevelOverflowException ex) {
            throw new MyException(COMMENT_HIERARCHY_TOO_DEEP, "评论层级最多不能超过5层。", "userId", getUserId());
        }
    }
}
