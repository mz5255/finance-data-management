package cn.com.mz.app.finance.datasource.mysql.mapper;

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
    List<SysRoleDO> selectRolesByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectRolePermissionByUserId(@Param("userId") String userId);
}