package org.ricky.common.constants;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className MessageConstants
 * @desc 接口返回的各类常量信息
 */
public interface MessageConstants {

    Integer PARAMS_ERROR_CODE = 1201;
    Integer PROCESS_ERROR_CODE = 1202;

    String PARAMS_IS_NOT_NULL = "参数是必需的！";
    String PARAMS_LENGTH_REQUIRED = "参数的长度必须符合要求！";
    String PARAMS_FORMAT_ERROR = "参数格式错误！";

    String OPERATE_FAILED = "操作失败！";
    String SUCCESS = "SUCCESS";

}
