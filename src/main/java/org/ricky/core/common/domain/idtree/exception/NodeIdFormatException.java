package org.ricky.core.common.domain.idtree.exception;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className NodeIdFormatException
 * @desc 节点ID格式错误
 */
public class NodeIdFormatException extends RuntimeException {
    public NodeIdFormatException(String message) {
        super(message);
    }
}
