package org.ricky.core.user.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.auth.PermissionEnum;
import org.ricky.core.common.domain.VO;
import org.ricky.core.user.domain.GenderEnum;
import org.ricky.core.user.domain.StatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className UserVO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserVO implements VO {

    String username;
    String mobile;
    String email;
    GenderEnum gender;
    String description;
    List<String> avatarList;
    String avatar;
    LocalDate birthday;
    StatusEnum status;
    PermissionEnum permission;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime lastLogin;

}
