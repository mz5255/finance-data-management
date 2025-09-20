package cn.com.mz.app.finance.module.service.auth;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;

/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.module.service.impl
 * @date 2025/9/14 22:54
 * @description: 功能描述
 */
public interface AuthService {
    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    BaseResult<?> register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     */
    BaseResult<UserInfo> query(UserQueryRequest userQueryRequest);
}
