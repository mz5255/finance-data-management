package cn.com.mz.app.finance.datasource.mysql.mapper.user;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

/**
 * @author 马震
 * @version 1.0
 * @date 2026/1/19 13:45
 * 用户相关
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
