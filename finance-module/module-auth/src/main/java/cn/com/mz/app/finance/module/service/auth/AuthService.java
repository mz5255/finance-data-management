package cn.com.mz.app.finance.module.service.auth;

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
    UserOperatorResponse register(UserRegisterRequest userRegisterRequest);
}
