package cn.com.mz.app.finance.datasource.mysql.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色菜单关联Mapper
 *
 * @author mz
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<Object> {

    /**
     * 删除角色菜单关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     * @return 影响行数
     */
    int batchInsert(@Param("roleId") Long roleId, @Param("menuIds") java.util.List<Long> menuIds);
}
