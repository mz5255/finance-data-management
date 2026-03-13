package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.RegisterParam;
import cn.com.mz.app.finance.module.dto.req.UpdateParam;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.vo.LoginReq;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.com.mz.app.finance.starter.utils.RedisUtils;
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
    @Operation(summary = "获取图片验证码", description = "获取图片验证码")
    public void captchaImage(@RequestParam String captchaKey) {
        authService.captchaImage(captchaKey);
    }

    @PostMapping("/register")
    @Operation(summary = "注册", description = "用户注册")
    public BaseResult<LoginReq> register(@Valid @RequestBody RegisterParam registerParam) {
        //验证码校验
        String cachedCode = redisUtils.get(CAPTCHA_KEY_PREFIX + registerParam.getCaptchaKey());
        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }

        //注册
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setTelephone(registerParam.getTelephone());
        userRegisterRequest.setPassword(registerParam.getPassword());

        BaseResult<?> registerResult = authService.register(userRegisterRequest);
        if(registerResult.isSuccess()){
            // 注册成功后自动登录
            LoginParam loginParam = new LoginParam();
            loginParam.setTelephone(registerParam.getTelephone());
            loginParam.setPassword(registerParam.getPassword());
            loginParam.setRememberMe(true);
            return authService.loginAfterRegister(loginParam);
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

    /**
     * 获取Token
     * @return Token信息
     * StpUtil.getLoginId() → 获取用户ID
     * StpUtil.getSession().get(userId) → 获取UserInfo对象
     * StpUtil.getTokenValue() → 获取Token字符串本身
     */
    @GetMapping("/token")
    @Operation(summary = "获取Token")
    public BaseResult<String> getToken() {
        String token = StpUtil.getTokenValue();
        return BaseResult.success(token);
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePassword")
    @Operation(summary = "修改密码")
    public BaseResult<Void> changePassword(@RequestBody UpdateParam params) {

        try {
            boolean success = authService.changePassword(params);
            return success ? BaseResult.success() : BaseResult.error("修改密码失败，原密码不正确");
        } catch (Exception e) {
            return BaseResult.error("修改密码失败：" + e.getMessage());
        }
    }
}
