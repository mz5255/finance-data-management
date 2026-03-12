package cn.com.mz.app.finance.application.webapi.system.permission;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import cn.com.mz.app.finance.datasource.mysql.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 用户权限Controller
 * 处理用户相关的权限查询接口
 *
 * @author mz
 */
@RestController
@RequestMapping("/api/finance-data/system/permission")
@Tag(name = "用户权限", description = "用户权限查询相关接口")
public class UserPermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 获取用户菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    @GetMapping("/getMenuTreeByUserId")
    @Operation(summary = "获取用户菜单树")
    public BaseResult<List<SysMenuDO>> getMenuTreeByUserId(@RequestParam Long userId) {
        List<SysMenuDO> menuTree = permissionService.getMenuTreeByUserId(userId);
        return BaseResult.success(menuTree);
    }

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/userRoles/{userId}")
    @Operation(summary = "获取用户角色列表")
    public BaseResult<List<String>> getUserRoles(@PathVariable Long userId) {
        Set<String> roles = permissionService.getRolePermission(userId);
        return BaseResult.success(roles.stream().toList());
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/userPermissions/{userId}")
    @Operation(summary = "获取用户权限列表")
    public BaseResult<List<String>> getUserPermissions(@PathVariable Long userId) {
        Set<String> permissions = permissionService.getMenuPermission(userId);
        return BaseResult.success(permissions.stream().toList());
    }

    /**
     * 检查用户是否有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    @GetMapping("/checkPermission")
    @Operation(summary = "检查用户权限")
    public BaseResult<Boolean> checkPermission(@RequestParam Long userId, @RequestParam String permission) {
        boolean hasPermission = permissionService.hasPermission(userId, permission);
        return BaseResult.success(hasPermission);
    }

    /**
     * 检查用户是否有指定角色
     *
     * @param userId 用户ID
     * @param roleKey 角色标识
     * @return 是否有角色
     */
    @GetMapping("/checkRole")
    @Operation(summary = "检查用户角色")
    public BaseResult<Boolean> checkRole(@RequestParam Long userId, @RequestParam String roleKey) {
        boolean hasRole = permissionService.hasRole(userId, roleKey);
        return BaseResult.success(hasRole);
    }

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    @PostMapping("/assignRoles")
    @Operation(summary = "为用户分配角色")
    public BaseResult<Boolean> assignRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        boolean success = permissionService.assignRolesToUser(userId, roleIds);
        return BaseResult.success(success);
    }
}
