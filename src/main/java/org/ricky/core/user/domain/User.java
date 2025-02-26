package org.ricky.core.user.domain;

import com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.domain.AggregateRoot;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.ricky.common.constants.ConfigConstant.USER_COLLECTION;

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
    private Boolean gender = null;

    /**
     * 简介
     */
    private String description;

    /**
     * 头像列表
     */
    private List<String> avatarList = new ArrayList<>();

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
    private Boolean banning = false;

    /**
     * 权限
     */
    private PermissionEnum permission;

    /**
     * 上次登录时间
     */
    private LocalDateTime lastLogin;

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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
