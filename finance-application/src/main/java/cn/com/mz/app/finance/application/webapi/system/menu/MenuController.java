package cn.com.mz.app.finance.application.webapi.system.menu;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.service.MenuService;
import cn.com.mz.app.finance.module.annotation.RequiresPermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理Controller
 *
 * @author mz
 */
@RestController
@RequestMapping("/api/finance-data/system/menu")
@Tag(name = "菜单管理", description = "菜单管理相关接口")
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    public BaseResult<List<SysMenuDO>> getMenuList() {
        List<SysMenuDO> menuList = menuService.getMenuList();
        return BaseResult.success(menuList);
    }

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public BaseResult<List<SysMenuDO>> getMenuTree() {
        List<SysMenuDO> menuTree = menuService.getMenuTree();
        return BaseResult.success(menuTree);
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "获取菜单详情")
    @RequiresPermissions("system:menu:query")
    public BaseResult<SysMenuDO> getMenuById(@PathVariable Long menuId) {
        SysMenuDO menu = menuService.getMenuById(menuId);
        return BaseResult.success(menu);
    }

    /**
     * 创建菜单
     *
     * @param menu 菜单信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "创建菜单")
    @RequiresPermissions("system:menu:add")
    public BaseResult<Boolean> createMenu(@RequestBody SysMenuDO menu) {
        boolean success = menuService.createMenu(menu);
        return BaseResult.success(success);
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新菜单")
    @RequiresPermissions("system:menu:edit")
    public BaseResult<Boolean> updateMenu(@RequestBody SysMenuDO menu) {
        boolean success = menuService.updateMenu(menu);
        return BaseResult.success(success);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除菜单")
    @RequiresPermissions("system:menu:remove")
    public BaseResult<Boolean> deleteMenu(@PathVariable Long menuId) {
        // 检查是否有子菜单
        if (menuService.hasChildByMenuId(menuId)) {
            return BaseResult.error(400, "存在子菜单,不允许删除");
        }
        boolean success = menuService.deleteMenu(menuId);
        return BaseResult.success(success);
    }

    /**
     * 获取角色菜单树（用于分配菜单权限）
     *
     * @param roleId 角色ID
     * @return 菜单树
     */
    @GetMapping("/roleMenuTree/{roleId}")
    @Operation(summary = "获取角色菜单树")
    @RequiresPermissions("system:role:edit")
    public BaseResult<List<SysMenuDO>> getRoleMenuTree(@PathVariable Long roleId) {
        List<SysMenuDO> menuTree = menuService.getMenuTree();
        List<Long> roleMenuIds = menuService.getMenuIdsByRoleId(roleId);
        // 这里可以标记哪些菜单被选中
        return BaseResult.success(menuTree);
    }
}
