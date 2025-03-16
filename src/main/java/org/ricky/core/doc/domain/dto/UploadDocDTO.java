package org.ricky.core.doc.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.ricky.core.common.domain.DTO;
import org.ricky.common.exception.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.ricky.common.exception.ErrorCodeEnum.FILE_MUST_NOT_BE_EMPTY;
import static org.ricky.common.exception.ErrorCodeEnum.FILE_ORIGINAL_NAME_MUST_NOT_BE_BLANK;
import static org.ricky.common.util.ValidationUtil.isBlank;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className UploadDocDTO
 * @desc
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadDocDTO implements DTO {

    @NotNull
    MultipartFile file;

    @Override
    public void correctAndValidate() {
        if (file.isEmpty()) {
            throw new MyException(FILE_MUST_NOT_BE_EMPTY, "文件不能为空",
                    Map.of("filename", file.getName()));
        }

        if (isBlank(file.getOriginalFilename())) {
            throw new MyException(FILE_ORIGINAL_NAME_MUST_NOT_BE_BLANK, "文件原始名称不能为空",
                    Map.of("filename", file.getName()));
        }
    }
}
