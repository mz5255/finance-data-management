package cn.com.mz.app.finance.datasource.mysql.service;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysMenuDO;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务类
 *
 * @author mz
 */
@Service
public class MenuService {

    @Resource
    private SysMenuMapper menuMapper;

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
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    public boolean createMenu(SysMenuDO menu) {
        return menuMapper.insert(menu) > 0;
    }

    /**
     * 更新菜单
     *
     * @param menu 菜单信息
     * @return 是否成功
     */
    public boolean updateMenu(SysMenuDO menu) {
        return menuMapper.updateById(menu) > 0;
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
}
