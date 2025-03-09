package org.ricky.core.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.result.ApiResult;
import org.ricky.core.common.validation.id.Id;
import org.ricky.core.user.domain.dto.RegistryUserDTO;
import org.ricky.core.user.domain.dto.UserLoginDTO;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.core.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.ricky.common.constants.ConfigConstant.USER_ID_PREFIX;

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

    @PostMapping("/registry/batch")
    @Operation(summary = "批量注册用户", description = "批量新增用户; 支持使用xls进行导入用户信息")
    public ApiResult<String> registryBatch(@RequestBody List<@Valid RegistryUserDTO> userDTOS) {
        return userService.registryBatch(userDTOS);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResult<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userDTO) {
        return userService.login(userDTO);
    }

    @GetMapping("/id/{userId}")
    @Operation(summary = "根据ID查询")
    public ApiResult<UserVO> getById(@PathVariable("userId") @Id(pre = USER_ID_PREFIX) String userId) {
        return userService.getById(userId);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询", description = "返回查询到的第一个用户")
    public ApiResult<UserVO> getByUsername(@PathVariable("username") String username) {
        return userService.getByUsername(username);
    }

}
