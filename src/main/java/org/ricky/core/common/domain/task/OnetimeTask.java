package org.ricky.core.common.domain.task;

/**
 * @author Ricky
 * @version 1.0
 * @date 2024/9/26
 * @className OnetimeTask
 * @desc Marker接口，无实际作用<br>
 * 保证一个Task只做一件事情，即只操作一种聚合，并且task的package跟着其所操作的聚合走<br>
 * 每个task接收原始数据类型<br>
 */
public interface OnetimeTask {
}
