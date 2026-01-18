package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.service.PermissionService;
import cn.com.mz.app.finance.module.annotation.RequiresPermissions;
import cn.com.mz.app.finance.module.annotation.RequiresRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 *
 * @author mz
 */
@RestController
@RequestMapping("/api/finance-data/system/permission")
@Tag(name = "权限管理", description = "用户权限分配相关接口")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    @PostMapping("/assignRoles")
    @Operation(summary = "为用户分配角色")
    @RequiresPermissions("system:user:edit")
    public BaseResult<Boolean> assignRolesToUser(@RequestParam String userId, @RequestBody List<Long> roleIds) {
        boolean success = permissionService.assignRolesToUser(userId, roleIds);
        return BaseResult.success(success);
    }

    /**
     * 为角色分配菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 操作结果
     */
    @PostMapping("/assignMenus")
    @Operation(summary = "为角色分配菜单权限")
    @RequiresRoles("admin")
    public BaseResult<Boolean> assignMenusToRole(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        boolean success = permissionService.assignMenusToRole(roleId, menuIds);
        return BaseResult.success(success);
    }

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/userRoles/{userId}")
    @Operation(summary = "获取用户角色")
    @RequiresPermissions("system:user:query")
    public BaseResult<List<String>> getUserRoles(@PathVariable String userId) {
        var roles = permissionService.getRolePermission(userId);
        return BaseResult.success(roles.stream().toList());
    }

    /**
     * 获取用户的权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/userPermissions/{userId}")
    @Operation(summary = "获取用户权限")
    @RequiresPermissions("system:user:query")
    public BaseResult<List<String>> getUserPermissions(@PathVariable String userId) {
        var permissions = permissionService.getMenuPermission(userId);
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
    public BaseResult<Boolean> checkPermission(@RequestParam String userId, @RequestParam String permission) {
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
    public BaseResult<Boolean> checkRole(@RequestParam String userId, @RequestParam String roleKey) {
        boolean hasRole = permissionService.hasRole(userId, roleKey);
        return BaseResult.success(hasRole);
    }

    /**
     * 获取用户所有菜单
     *
     * @param userId 用户ID
     * @return 是否有角色
     */
    @GetMapping("/getMenuTreeByUserId")
    @Operation(summary = "获取用户所有菜单")
    public BaseResult<?> getMenuTreeByUserId(@RequestParam String userId) {
        List<SysMenuDO> menuTreeByUserId = permissionService.getMenuTreeByUserId(userId);
        return BaseResult.success(menuTreeByUserId);
    }
}