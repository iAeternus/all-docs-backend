package org.ricky.core.user.domain;

import com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.domain.AggregateRoot;
import org.ricky.common.exception.MyException;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.time.LocalDateTime.now;
import static org.ricky.common.auth.PermissionEnum.ADMIN;
import static org.ricky.common.constants.ConfigConstant.USER_COLLECTION;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.common.exception.ErrorCodeEnum.USER_ALREADY_DEACTIVATED;
import static org.ricky.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static org.ricky.common.util.ValidationUtil.*;
import static org.ricky.core.user.domain.GenderEnum.UNKNOWN;
import static org.ricky.core.user.domain.StatusEnum.DISABLE;
import static org.ricky.core.user.domain.StatusEnum.ENABLE;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className User
 * @desc 用户
 */
@Getter
@Document(USER_COLLECTION)
@TypeAlias(USER_COLLECTION)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends AggregateRoot {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别，默认为空，没有性别
     */
    private GenderEnum gender;

    /**
     * 简介
     */
    private String description;

    /**
     * 头像列表
     */
    private List<String> avatarList;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 封禁状态
     */
    private StatusEnum status;

    /**
     * 权限
     */
    private PermissionEnum permission;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLogin;

    public User(String id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
        this.permission = ADMIN;
        addOpsLog("新建");
    }

    public User(String username, String password, PermissionEnum permission) {
        super(newUserId());
        this.username = username;
        this.password = password;
        this.gender = UNKNOWN;
        this.avatarList = new ArrayList<>();
        this.status = ENABLE;
        this.permission = permission;
        this.lastLogin = now();
        addOpsLog("新建");
    }

    public static String newUserId() {
        return USER_ID_PREFIX + newSnowflakeId();
    }

    /**
     * 校验用户权限
     *
     * @param permissionEnums 权限数组
     * @return true=有权限 false=无权限
     */
    public boolean checkPermission(PermissionEnum[] permissionEnums) {
        return Arrays.stream(permissionEnums)
                .collect(toImmutableSet())
                .contains(permission);
    }

    public boolean isActivate() {
        return status == ENABLE;
    }

    public boolean isDeactivate() {
        return status == DISABLE;
    }

    public void activate() {
        status = ENABLE;
        addOpsLog("启用");
    }

    public void deactivate() {
        status = DISABLE;
        addOpsLog("禁用");
    }

    public void checkActive() {
        if (isDeactivate()) {
            throw new MyException(USER_ALREADY_DEACTIVATED, "当前用户已经被禁用。", Map.of("userId", getId()));
        }
    }

    public String getType() {
        return isNull(permission) ? null : permission.toString();
    }

    public void update(String password, String mobile, String email, GenderEnum gender, String description, LocalDate birthday) {
        this.password = isNotBlank(password) ? password : this.password;
        this.mobile = isNotBlank(mobile) ? mobile : this.mobile;
        this.email = isNotBlank(email) ? email : this.email;
        this.gender = nonNull(gender) ? gender : this.gender;
        this.description = isNotBlank(description) ? description : this.description;
        this.birthday = nonNull(birthday) ? birthday : this.birthday;
        addOpsLog("变更");
    }

    public void onDelete() {
        addOpsLog("删除");
    }

    public void updateRole(PermissionEnum newRole) {
        this.permission = newRole;
        addOpsLog("变更权限");
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
