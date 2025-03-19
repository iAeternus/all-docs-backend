package org.ricky.core.common.domain.idtree.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.ricky.core.common.domain.idtree.IdTree;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className NodeIdValidator
 * @desc 节点ID校验器
 */
public class NodeIdValidator implements ConstraintValidator<NodeId, String> {
    @Override
    public boolean isValid(String nodeId, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(nodeId)) {
            return true;
        }

        return !nodeId.contains(IdTree.NODE_ID_SEPARATOR);
    }
}
