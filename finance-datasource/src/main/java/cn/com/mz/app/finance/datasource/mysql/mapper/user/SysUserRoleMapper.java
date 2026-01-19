package cn.com.mz.app.finance.datasource.mysql.mapper.user;

import cn.com.mz.app.finance.datasource.mysql.entity.permission.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper
 *
 * @author mz
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 删除用户角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") String userId);

    /**
     * 批量插入用户角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int batchInsert(@Param("userId") String userId, @Param("roleIds") List<Long> roleIds);
}
