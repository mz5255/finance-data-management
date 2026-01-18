package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.aspect.mobile.IsMobile;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.RegisterParam;
import cn.com.mz.app.finance.module.dto.req.UserQueryRequest;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.vo.LoginReq;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.com.mz.app.finance.starter.utils.RedisUtils;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    private RedisUtils redisUtils;
    @Resource
    private AuthService authService;


    @GetMapping("/captchaImage")
    public void captchaImage(@IsMobile String telephone) {
        authService.captchaImage(telephone);
    }

    @PostMapping("/register")
    @Operation(summary = "注册", description = "用户注册")
    public BaseResult<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {
        //验证码校验
        String cachedCode = redisUtils.get(CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
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
        return authService.login(loginParam);
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
