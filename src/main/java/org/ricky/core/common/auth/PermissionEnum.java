package org.ricky.core.common.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.ricky.core.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className PermissionEnum
 * @desc 权限枚举
 */
@Getter
@AllArgsConstructor
public enum PermissionEnum {

    USER(1, "用户权限"),
    ADMIN(2, "管理员管理权限"),
    NO(-99999, "无需权限"),
    ;

    /**
     * 权限编码
     */
    private final Integer code;

    /**
     * 权限名称
     */
    private final String msg;

    public static PermissionEnum getRoleByName(String name) {
        if (isBlank(name)) {
            return null;
        }
        for (PermissionEnum value : PermissionEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
