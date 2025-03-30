package org.ricky.core.commenthierarchy.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.idtree.IdTree;
import org.ricky.core.common.domain.marker.VO;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/30
 * @className CommentHierarchyVO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CommentHierarchyVO implements VO {

    IdTree idTree;

    List<CommentVO> allComments;

    @Value
    @Builder
    @AllArgsConstructor(access = PRIVATE)
    public static class CommentVO {
        String id;
        String content;
    }
}
