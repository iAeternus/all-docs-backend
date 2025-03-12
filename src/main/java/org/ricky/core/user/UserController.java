package org.ricky.core.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ricky.common.auth.Permission;
import org.ricky.common.domain.PageDTO;
import org.ricky.common.domain.PageVO;
import org.ricky.common.result.ApiResult;
import org.ricky.core.common.validation.id.Id;
import org.ricky.core.user.domain.dto.*;
import org.ricky.core.user.domain.vo.UserLoginVO;
import org.ricky.core.user.domain.vo.UserVO;
import org.ricky.core.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.ricky.common.auth.PermissionEnum.ADMIN;
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
    public ApiResult<Boolean> registryBatch(@RequestBody List<@Valid RegistryUserDTO> userDTOS) {
        return userService.registryBatch(userDTOS);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResult<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userDTO) {
        return userService.login(userDTO);
    }

    @PutMapping
    @Operation(summary = "变更用户信息")
    public ApiResult<Boolean> updateById(@RequestBody @Valid UserDTO userDTO) {
        return userService.updateById(userDTO);
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

    @Permission(ADMIN)
    @DeleteMapping("/{userId}")
    @Operation(summary = "根据ID删除用户", description = "需要管理员权限")
    public ApiResult<Boolean> deleteById(@PathVariable("userId") @Id(pre = USER_ID_PREFIX) String userId) {
        return userService.deleteById(userId);
    }

    @Permission(ADMIN)
    @DeleteMapping("/batch")
    @Operation(summary = "根据ID批量删除用户", description = "需要管理员权限")
    public ApiResult<Boolean> deleteByIdBatch(@RequestBody DeleteByIdBatchDTO dto) {
        return userService.deleteByIdBatch(dto);
    }

    @GetMapping("/login/state")
    @Operation(summary = "检查登录状态", description = "返回true代表已登录")
    public ApiResult<Boolean> checkLoginState(HttpServletRequest request, HttpServletResponse response) {
        return userService.checkLoginState(request, response);
    }

    @Permission(ADMIN)
    @GetMapping("/page")
    @Operation(summary = "分页查询所有用户", description = "需要管理员权限")
    public ApiResult<PageVO<UserVO>> page(@RequestBody @Valid PageDTO pageDTO) {
        return userService.page(pageDTO);
    }

    @Permission(ADMIN)
    @PutMapping("/role")
    @Operation(summary = "变更用户权限", description = "需要管理员权限")
    public ApiResult<Boolean> updateRole(@RequestBody @Valid UpdateRoleDTO dto) {
        return userService.updateRole(dto);
    }

    @Permission(ADMIN)
    @PutMapping("/deactivate/{userId}")
    @Operation(summary = "封禁用户", description = "需要管理员权限")
    public ApiResult<Boolean> deactivate(@PathVariable("userId") @Id(pre = USER_ID_PREFIX) String userId) {
        return userService.deactivate(userId);
    }

    @Permission(ADMIN)
    @PutMapping("/activate/{userId}")
    @Operation(summary = "解封用户", description = "需要管理员权限")
    public ApiResult<Boolean> active(@PathVariable("userId") @Id(pre = USER_ID_PREFIX) String userId) {
        return userService.activate(userId);
    }

    @PostMapping("/avatar/{userId}")
    @Operation(summary = "上传头像")
    public ApiResult<Boolean> uploadAvatar(@PathVariable("userId") @Id(pre = USER_ID_PREFIX) String userId,
                                           @RequestParam("img") MultipartFile img) {
        return userService.uploadAvatar(userId, img);
    }

}
