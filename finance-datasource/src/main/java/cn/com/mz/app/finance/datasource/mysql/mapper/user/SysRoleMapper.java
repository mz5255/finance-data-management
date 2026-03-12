package cn.com.mz.app.finance.datasource.mysql.mapper.user;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysRoleDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色信息 数据层
 *
 * @author mz
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleDO> {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleDO> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectRolePermissionByUserId(@Param("userId") Long userId);

    /**
     * 检查角色名称唯一性
     *
     * @param roleName 角色名称
     * @return 角色信息（如果存在）
     */
    SysRoleDO checkRoleNameUnique(@Param("roleName") String roleName);

    /**
     * 检查角色标识唯一性
     *
     * @param roleKey 角色标识
     * @return 角色信息（如果存在）
     */
    SysRoleDO checkRoleKeyUnique(@Param("roleKey") String roleKey);
}
