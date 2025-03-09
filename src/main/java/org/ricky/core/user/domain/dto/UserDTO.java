package org.ricky.core.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.domain.DTO;
import org.ricky.core.user.domain.GenderEnum;

import java.time.LocalDate;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/9
 * @className UserDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDTO implements DTO {

    String id;
    String password;
    String mobile;
    String email;
    GenderEnum gender;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDate birthday;

}
