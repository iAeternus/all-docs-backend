package org.ricky.core.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.auth.Permission;
import org.ricky.common.auth.PermissionEnum;
import org.ricky.common.result.ApiResult;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.ricky.common.auth.PermissionEnum.NO;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/2/26
 * @className UserController
 * @desc 用户模块
 */
@Slf4j
@Validated
@CrossOrigin
@RestController
@Tag(name = "用户模块")
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/registry")
    @Operation(summary = "注册单个用户", description = "注册单个用户")
    public ApiResult<String> registry(@RequestBody @Valid RegistryUserDTO userDTO) {
        return userService.registry(userDTO);
    }

}
