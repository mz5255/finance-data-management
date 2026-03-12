package cn.com.mz.app.finance.application.webapi.system.role;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import cn.com.mz.app.finance.datasource.mysql.service.MenuService;
import cn.com.mz.app.finance.datasource.mysql.service.PermissionService;
import cn.com.mz.app.finance.datasource.mysql.service.RoleService;
import cn.com.mz.app.finance.module.annotation.RequiresPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 *
 * @author mz
 */
@RestController
@RequestMapping("/api/finance-data/system/role")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private MenuService menuService;

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    public BaseResult<List<SysRoleDO>> getRoleList() {
        List<SysRoleDO> roleList = roleService.getRoleList();
        return BaseResult.success(roleList);
    }

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情")
    @RequiresPermissions("system:role:query")
    public BaseResult<SysRoleDO> getRoleById(@PathVariable Long roleId) {
        SysRoleDO role = roleService.getRoleById(roleId);
        return BaseResult.success(role);
    }

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "创建角色")
    @RequiresPermissions("system:role:add")
    public BaseResult<Boolean> createRole(@RequestBody SysRoleDO role) {
        // 检查角色名称唯一性
        SysRoleDO existingRole = roleService.checkRoleNameUnique(role.getRoleName());
        if (existingRole != null) {
            return BaseResult.error(400, "角色名称已存在");
        }
        // 检查角色标识唯一性
        existingRole = roleService.checkRoleKeyUnique(role.getRoleKey());
        if (existingRole != null) {
            return BaseResult.error(400, "角色标识已存在");
        }
        boolean success = roleService.createRole(role);
        return BaseResult.success(success);
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新角色")
    @RequiresPermissions("system:role:edit")
    public BaseResult<Boolean> updateRole(@RequestBody SysRoleDO role) {
        boolean success = roleService.updateRole(role);
        return BaseResult.success(success);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    @RequiresPermissions("system:role:remove")
    public BaseResult<Boolean> deleteRole(@PathVariable Long roleId) {
        boolean success = roleService.deleteRole(roleId);
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
    @RequiresPermissions("system:role:edit")
    public BaseResult<Boolean> assignMenus(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        boolean success = permissionService.assignMenusToRole(roleId, menuIds);
        return BaseResult.success(success);
    }

    /**
     * 获取角色的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/menuIds/{roleId}")
    @Operation(summary = "获取角色的菜单ID列表")
    @RequiresPermissions("system:role:query")
    public BaseResult<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = menuService.getMenuIdsByRoleId(roleId);
        return BaseResult.success(menuIds);
    }
}
