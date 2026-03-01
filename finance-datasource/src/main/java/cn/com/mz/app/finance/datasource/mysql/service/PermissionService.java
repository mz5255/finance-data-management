package cn.com.mz.app.finance.datasource.mysql.service;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysMenuMapper;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysRoleMapper;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysRoleMenuMapper;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务类
 *
 * @author mz
 */
@Service
public class PermissionService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 获取用户角色权限
     *
     * @param userId 用户ID
     * @return 角色权限集合
     */
    public Set<String> getRolePermission(Long userId) {
        List<SysRoleDO> roles = roleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(SysRoleDO::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户菜单权限
     *
     * @param userId 用户ID
     * @return 菜单权限集合
     */
    public Set<String> getMenuPermission(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        return perms.stream()
                .filter(perm -> perm != null && !perm.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 根据用户ID查询菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    public List<SysMenuDO> getMenuTreeByUserId(Long userId) {
        List<SysMenuDO> menus = menuMapper.selectMenuTreeByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<SysMenuDO> buildMenuTree(List<SysMenuDO> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .peek(menu -> menu.setChildren(buildMenuTree(menus, menu.getMenuId())))
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    public boolean hasPermission(Long userId, String permission) {
        Set<String> permissions = getMenuPermission(userId);
        return permissions.contains(permission);
    }

    /**
     * 检查用户是否有指定角色
     *
     * @param userId 用户ID
     * @param roleKey 角色标识
     * @return 是否有角色
     */
    public boolean hasRole(Long userId, String roleKey) {
        Set<String> roles = getRolePermission(userId);
        return roles.contains(roleKey);
    }

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户现有角色
        userRoleMapper.deleteByUserId(userId);

        // 如果角色列表不为空，则批量插入新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleMapper.batchInsert(userId, roleIds);
        }

        return true;
    }

    /**
     * 为角色分配菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenusToRole(Long roleId, List<Long> menuIds) {
        // 先删除角色现有权限
        roleMenuMapper.deleteByRoleId(roleId);

        // 如果菜单列表不为空，则批量插入新权限
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMenuMapper.batchInsert(roleId, menuIds);
        }

        return true;
    }
}
