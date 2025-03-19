package org.ricky.core.common.domain.idtree.exception;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className IdNodeLevelOverflowException
 * @desc 树的高度超限
 */
public class IdNodeLevelOverflowException extends RuntimeException {
    public IdNodeLevelOverflowException(String message) {
        super(message);
    }
}
