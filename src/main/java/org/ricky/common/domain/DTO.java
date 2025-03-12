package org.ricky.common.domain;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className DTO
 * @desc 数据传输对象<br>
 * 规范：<br>
 * 1. 实现类非必要不可变<br>
 * 2. 实现类必须支持建造者模式<br>
 * 3. 实现类必须将全参构造私有<br>
 */
public interface DTO {

    default void correctAndValidate() {
    }

}
