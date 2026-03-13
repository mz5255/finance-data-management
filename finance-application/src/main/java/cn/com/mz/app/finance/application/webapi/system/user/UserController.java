package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.dto.UserDTO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import cn.com.mz.app.finance.module.dto.req.UpdateParam;
import cn.com.mz.app.finance.module.setvice.CustomerService;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.application.webapi.system.user
 * @date 2026/3/1 18:55
 * @description: 功能描述
 */
@RestController
@RequestMapping("/api/finance-data/user")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    @Resource
    private CustomerService userService;

    @Resource
    private UserService baseUserService;

    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @GetMapping("/info")
    public BaseResult<UserDTO> getCurrentUserInfo() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId == null) {
            return BaseResult.error("用户未登录");
        }
        Long userId = Long.valueOf(loginId.toString());
        UserDO user = baseUserService.getById(userId);
        if (user == null) {
            return BaseResult.error("用户不存在");
        }
        UserDTO userDTO = UserDTO.build(user);
        return BaseResult.success(userDTO);
    }

    /**
     * 分页查询用户列表（仅正常状态用户，供前台使用）
     */
    @Operation(summary = "分页查询用户列表", description = "仅查询状态为 INIT 或 AUTH 的用户")
    @PostMapping("/page")
    public BaseResult<IPage<UserDTO>> getUserList(@RequestBody QueryParam queryParam) {
        IPage<UserDTO> membersByPage = userService.getMembersByPage(queryParam);
        return BaseResult.success(membersByPage);
    }

    /**
     * 分页查询所有用户列表（包括冻结用户，供管理后台使用）
     */
    @Operation(summary = "分页查询所有用户", description = "查询所有状态的用户，包括冻结用户")
    @PostMapping("/admin/page")
    public BaseResult<IPage<UserDTO>> getAllUsers(@RequestBody QueryParam queryParam) {
        IPage<UserDTO> allUsersByPage = userService.getAllUsersByPage(queryParam);
        return BaseResult.success(allUsersByPage);
    }

    /**
     * 更改用户状态
     */
    @Operation(summary = "更改用户状态", description = "更改用户状态：INIT、AUTH、FROZEN")
    @PutMapping("/admin/state")
    public BaseResult<Void> updateUserState(@RequestParam Long userId, @RequestParam String state) {
        UserStateEnum stateEnum = UserStateEnum.getByValue(state);
        boolean success = userService.updateStateById(userId, stateEnum);
        return success ? BaseResult.success() : BaseResult.error("更改用户状态失败");
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{userId}")
    public BaseResult<Void> deleteUser(@PathVariable Long userId) {
        boolean success = userService.deleteById(userId);
        return success ? BaseResult.success() : BaseResult.error("删除用户失败");
    }

    /**
     * 更新用户个人信息
     */
    @Operation(summary = "更新个人信息", description = "更新用户昵称和手机号")
    @PutMapping("/profile")
    public BaseResult<Void> updateProfile(@RequestBody UpdateParam updateParam) {
        try {
            boolean success = userService.updateProfile(updateParam);
            return success ? BaseResult.success() : BaseResult.error("更新个人信息失败");
        } catch (IllegalArgumentException e) {
            return BaseResult.error(e.getMessage());
        }
    }
}
