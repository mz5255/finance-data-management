package cn.com.mz.app.finance.datasource.mysql.mapper.user;

import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 * <p>
 * 提供用户实体的数据库操作方法，继承 MyBatis-Plus 的 BaseMapper 获得基础 CRUD 能力
 *
 * @author 马震
 * @version 1.0
 * @since 2026/1/19 13:45
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
