package cn.com.mz.app.finance.application.webapi;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.RegisterParam;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.vo.LoginReq;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.com.mz.app.finance.starter.constant.CacheConstant.CAPTCHA_KEY_PREFIX;


/**
 * @author mz
 * @project finance-data-management
 * @package cn.com.mz.app.finance.application.webapi
 * @date 2025/9/14 22:15
 * @description: 功能描述
 */
@RestController
@RequestMapping("/api/finance-data/auth")
@Tag(name = "权限模块", description = "用户登录注册相关接口")
public class AuthController {

    @Resource
    private StringRedisTemplate redisTemplate;
    @Resource
    private AuthService authService;

    private static final String ROOT_CAPTCHA = "5255";
    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;


    @PostMapping("/register")
    @Operation(summary = "注册", description = "用户注册")
    public BaseResult<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {
        //验证码校验
        String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }

        //注册
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setTelephone(registerParam.getTelephone());

        BaseResult<?> registerResult = authService.register(userRegisterRequest);
        if(registerResult.isSuccess()){
            return BaseResult.success(true);
        }
        return BaseResult.error(registerResult.getCode(), registerResult.getMessage());
    }

    /**
     * 登录方法
     *
     * @return 结果
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public BaseResult<LoginReq> login(@Valid @RequestBody LoginParam loginParam) {
        //fixme 为了方便，暂时直接跳过
        if (!ROOT_CAPTCHA.equals(loginParam.getCaptcha())) {
            //验证码校验
            String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + loginParam.getTelephone());
            if (!StringUtils.equalsIgnoreCase(cachedCode, loginParam.getCaptcha())) {
                throw new BusinessException("验证码错误");
            }
        }

        //判断是注册还是登陆
        //查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
        BaseResult<UserInfo> userQueryResponse = authService.query(userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            //需要注册
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            userRegisterRequest.setTelephone(loginParam.getTelephone());

            BaseResult<?> response = authService.register(userRegisterRequest);
            if (response.isSuccess()) {
                userQueryResponse = authService.query(userQueryRequest);
                userInfo = userQueryResponse.getData();
                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
                LoginReq loginVO = new LoginReq(userInfo);
                return BaseResult.success(loginVO);
            }

            return BaseResult.error(response.getCode(), response.getMessage());
        } else {
            //登录
            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
            LoginReq loginVO = new LoginReq(userInfo);
            return BaseResult.success(loginVO);
        }
    }

    /**
     * 登出方法
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "登出")
    public BaseResult<Boolean> logout() {
        StpUtil.logout();
        return BaseResult.success(true);
    }
}
