package cn.com.mz.app.finance.datasource.mysql.service;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import cn.com.mz.app.finance.datasource.mysql.mapper.user.SysRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务类
 *
 * @author mz
 */
@Service
public class RoleService {

    @Resource
    private SysRoleMapper roleMapper;

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    public List<SysRoleDO> getRoleList() {
        return roleMapper.selectList(
                new LambdaQueryWrapper<SysRoleDO>()
                        .eq(SysRoleDO::getDelFlag, "0")
                        .orderByAsc(SysRoleDO::getRoleSort)
        );
    }

    /**
     * 根据角色ID获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    public SysRoleDO getRoleById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    public boolean createRole(SysRoleDO role) {
        return roleMapper.insert(role) > 0;
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    public boolean updateRole(SysRoleDO role) {
        return roleMapper.updateById(role) > 0;
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    public boolean deleteRole(Long roleId) {
        return roleMapper.deleteById(roleId) > 0;
    }

    /**
     * 检查角色名称唯一性
     *
     * @param roleName 角色名称
     * @return 角色信息（如果存在）
     */
    public SysRoleDO checkRoleNameUnique(String roleName) {
        return roleMapper.checkRoleNameUnique(roleName);
    }

    /**
     * 检查角色标识唯一性
     *
     * @param roleKey 角色标识
     * @return 角色信息（如果存在）
     */
    public SysRoleDO checkRoleKeyUnique(String roleKey) {
        return roleMapper.checkRoleKeyUnique(roleKey);
    }
}
