package cn.com.mz.app.finance.module.service.auth;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.UpdateParam;
import cn.com.mz.app.finance.module.vo.LoginReq;
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

    void captchaImage(String captchaKey);

    BaseResult<LoginReq> login(LoginParam loginParam);

    /**
     * 注册后自动登录
     *
     * @param loginParam
     * @return
     */
    BaseResult<LoginReq> loginAfterRegister(LoginParam loginParam);

    /**
     * 修改密码
     *
     * @return 是否修改成功
     */
    boolean changePassword(UpdateParam params);
}
