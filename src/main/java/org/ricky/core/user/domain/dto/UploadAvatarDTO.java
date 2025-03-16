package org.ricky.core.user.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.common.exception.MyException;
import org.ricky.core.common.domain.DTO;
import org.ricky.core.common.validation.id.Id;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static java.util.Arrays.stream;
import static org.ricky.common.constants.ConfigConstant.AVATAR_TYPES;
import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;
import static org.ricky.common.exception.ErrorCodeEnum.FILE_MUST_NOT_BE_EMPTY;
import static org.ricky.common.exception.ErrorCodeEnum.UNSUPPORTED_FILE_TYPES;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/12
 * @className UploadAvatarDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadAvatarDTO implements DTO {

    @NotBlank
    @Id(pre = USER_ID_PREFIX)
    String userId;

    @NotNull
    MultipartFile img;

    @Override
    public void correctAndValidate() {
        if (img.isEmpty()) {
            throw new MyException(FILE_MUST_NOT_BE_EMPTY, "文件不能为空",
                    Map.of("filename", img.getName()));
        }

        if (stream(AVATAR_TYPES).noneMatch(type -> type.equals(img.getContentType()))) {
            throw new MyException(UNSUPPORTED_FILE_TYPES, "不支持的文件类型",
                    Map.of("filename", img.getName(), "contentType", img.getContentType()));
        }
    }
}
