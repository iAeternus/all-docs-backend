package org.ricky.core.common.domain.idtree.exception;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/12
 * @className IdNodeNotFoundException
 * @desc 节点未找到
 */
public class IdNodeNotFoundException extends RuntimeException {
    public IdNodeNotFoundException(String message) {
        super(message);
    }
}
