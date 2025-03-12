package org.ricky.common.context;

import lombok.Data;
import org.ricky.common.util.ValidationUtil;

import static org.ricky.common.util.ValidationUtil.requireNotBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserContext
 * @desc 用户上下文，使用ThreadLocal传递
 */
@Data
public class UserContext {

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 用户名
     */
    private String username;

    private UserContext(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public static UserContext of(String uid, String username) {
        return new UserContext(uid, username);
    }

    public boolean isSelf(String uid) {
        requireNotBlank(uid, "uid must not be null");
        return ValidationUtil.equals(uid, this.uid);
    }

}
