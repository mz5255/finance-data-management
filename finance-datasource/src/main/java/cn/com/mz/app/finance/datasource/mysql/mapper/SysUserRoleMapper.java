package cn.com.mz.app.finance.datasource.mysql.mapper;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联Mapper
 *
 * @author mz
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<UserDO> {

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
    int batchInsert(@Param("userId") String userId, @Param("roleIds") java.util.List<Long> roleIds);
}