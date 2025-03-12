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
    String PARAMS_TYPE_ERROR = "类型转换错误";
    String DATA_HAS_EXIST = "数据已经存在！";
    String DATA_IS_NULL = "数据为空！";
    String FORMAT_ERROR = "格式不支持！";
    String DATA_DUPLICATE = "已经重复！";
    String REQUEST_METHOD_ERROR = "请求方法不对！";
    String FILE_SIZE_ERROR = "上传的文件超过大小限制!";
    String USER_HAS_BANNED = "该账号已经被屏蔽，请联系管理员！";
    String OPERATE_FAILED = "操作失败！";
    String FILE_NOT_FOUND = "File was not found";

}
