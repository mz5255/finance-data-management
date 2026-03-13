package cn.com.mz.app.finance.datasource.mysql.service;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysMenuMapper;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务类
 *
 * @author mz
 */
@Service
public class MenuService {

    /**
     * 超级管理员角色标识
     */
    private static final String SUPER_ADMIN_ROLE_KEY = "admin";

    @Resource
    private SysMenuMapper menuMapper;

    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    @Resource
    private RoleService roleService;

    /**
     * 获取所有菜单列表
     *
     * @return 菜单列表
     */
    public List<SysMenuDO> getMenuList() {
        return menuMapper.selectList(
                new LambdaQueryWrapper<SysMenuDO>()
                        .orderByAsc(SysMenuDO::getParentId, SysMenuDO::getOrderNum)
        );
    }

    /**
     * 获取菜单树形结构
     *
     * @return 菜单树
     */
    public List<SysMenuDO> getMenuTree() {
        List<SysMenuDO> menuList = getMenuList();
        return buildMenuTree(menuList);
    }

    /**
     * 构建菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    public List<SysMenuDO> buildMenuTree(List<SysMenuDO> menuList) {
        List<SysMenuDO> returnList = new ArrayList<>();
        List<Long> tempList = menuList.stream().map(SysMenuDO::getMenuId).collect(Collectors.toList());

        for (SysMenuDO menu : menuList) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menuList, menu);
                returnList.add(menu);
            }
        }

        if (returnList.isEmpty()) {
            returnList = menuList;
            for (SysMenuDO menu : returnList) {
                recursionFn(menuList, menu);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenuDO> list, SysMenuDO t) {
        // 得到子节点列表
        List<SysMenuDO> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenuDO tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenuDO> getChildList(List<SysMenuDO> list, SysMenuDO t) {
        List<SysMenuDO> tlist = new ArrayList<>();
        for (SysMenuDO n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenuDO> list, SysMenuDO t) {
        return !getChildList(list, t).isEmpty();
    }

    /**
     * 根据菜单ID获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    public SysMenuDO getMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }

    /**
     * 创建菜单
     * 创建后自动分配给超级管理员
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(SysMenuDO menu) {
        boolean success = menuMapper.insert(menu) > 0;
        if (success) {
            // 自动分配给超级管理员
            assignMenuToSuperAdmin(menu.getMenuId());
        }
        return success;
    }

    /**
     * 更新菜单
     * 更新后确保超级管理员拥有该菜单权限
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenuDO menu) {
        boolean success = menuMapper.updateById(menu) > 0;
        if (success) {
            // 确保超级管理员拥有该菜单权限
            assignMenuToSuperAdmin(menu.getMenuId());
        }
        return success;
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    public boolean deleteMenu(Long menuId) {
        return menuMapper.deleteById(menuId) > 0;
    }

    /**
     * 检查是否有子菜单
     *
     * @param menuId 菜单ID
     * @return 是否有子菜单
     */
    public boolean hasChildByMenuId(Long menuId) {
        return menuMapper.hasChildByMenuId(menuId);
    }

    /**
     * 根据角色ID获取菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 将菜单分配给超级管理员
     * 超级管理员默认拥有所有菜单权限
     *
     * @param menuId 菜单ID
     */
    private void assignMenuToSuperAdmin(Long menuId) {
        // 获取超级管理员角色
        SysRoleDO superAdminRole = roleService.getRoleByKey(SUPER_ADMIN_ROLE_KEY);
        if (superAdminRole == null) {
            throw new RuntimeException("超级管理员角色不存在，请检查系统初始化数据");
        }

        Long superAdminRoleId = superAdminRole.getRoleId();

        // 检查超级管理员是否已拥有该菜单权限
        List<Long> adminMenuIds = getMenuIdsByRoleId(superAdminRoleId);
        if (!adminMenuIds.contains(menuId)) {
            // 批量插入角色菜单关联（单个菜单）
            roleMenuMapper.batchInsert(superAdminRoleId, List.of(menuId));
        }
    }
}
