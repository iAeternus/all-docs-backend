package org.ricky.core.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.marker.DTO;
import org.ricky.core.common.validation.id.Id;
import org.ricky.core.common.validation.mobile.Mobile;
import org.ricky.core.common.validation.password.Password;
import org.ricky.core.user.domain.GenderEnum;

import java.time.LocalDate;

import static org.ricky.common.constants.ConfigConstant.MAX_GENERIC_TEXT_LENGTH;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;

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

    @NotBlank
    @Id(pre = USER_ID_PREFIX)
    String id;

    @NotBlank
    @Password
    String password;

    @Mobile
    String mobile;

    @Email
    String email;

    @NotNull
    GenderEnum gender;

    @Size(max = MAX_GENERIC_TEXT_LENGTH)
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;

}
