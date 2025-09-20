package cn.com.mz.app.finance.datasource.mysql.mapper;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author mz
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}