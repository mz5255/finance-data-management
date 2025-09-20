//package cn.com.mz.app.finance.application.webapi;
//
//import cn.com.mz.app.finance.common.dto.base.BaseResult;
//import cn.com.mz.app.finance.common.exceptions.BusinessException;
//import cn.com.mz.app.finance.module.dto.req.RegisterParam;
//import cn.com.mz.app.finance.module.service.auth.AuthService;
//import cn.com.mz.app.finance.module.service.auth.UserOperatorResponse;
//import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
//import cn.com.mz.app.finance.starter.constant.CacheConstant;
//import jakarta.validation.Valid;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//
///**
// * @author mz
// * @project finance-data-management
// * @package cn.com.mz.app.finance.application.webapi
// * @date 2025/9/14 22:15
// * @description: 功能描述
// */
//@RestController
//@RequestMapping("/api/finance-data/auth")
//public class AuthController {
//
//    @Resource
//    private StringRedisTemplate redisTemplate;
//    @Resource
//    private AuthService authService;
//
//
//    @PostMapping("/register")
//    public BaseResult<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {
//
//        //验证码校验
//        String cachedCode = redisTemplate.opsForValue().get(CacheConstant.CAPTCHA_KEY_PREFIX + registerParam.getTelephone());
//        if (!StringUtils.equalsIgnoreCase(cachedCode, registerParam.getCaptcha())) {
//            throw new BusinessException("验证码错误");
//        }
//
//        //注册
//        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
//        userRegisterRequest.setTelephone(registerParam.getTelephone());
//        userRegisterRequest.setInviteCode(registerParam.getInviteCode());
//
//        UserOperatorResponse registerResult = authService.register(userRegisterRequest);
//        if(registerResult.isSuccess()){
//            return BaseResult.success(true);
//        }
//        return BaseResult.error(registerResult.getCode(), registerResult.getMessage());
//    }
//
//    /**
//     * 登录方法
//     *
//     * @return 结果
//     */
//    @PostMapping("/login")
//    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {
//        //fixme 为了方便，暂时直接跳过
//        if (!ROOT_CAPTCHA.equals(loginParam.getCaptcha())) {
//            //验证码校验
//            String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + loginParam.getTelephone());
//            if (!StringUtils.equalsIgnoreCase(cachedCode, loginParam.getCaptcha())) {
//                throw new AuthException(VERIFICATION_CODE_WRONG);
//            }
//        }
//
//        //判断是注册还是登陆
//        //查询用户信息
//        UserQueryRequest userQueryRequest = new UserQueryRequest(loginParam.getTelephone());
//        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
//        UserInfo userInfo = userQueryResponse.getData();
//        if (userInfo == null) {
//            //需要注册
//            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
//            userRegisterRequest.setTelephone(loginParam.getTelephone());
//            userRegisterRequest.setInviteCode(loginParam.getInviteCode());
//
//            UserOperatorResponse response = userFacadeService.register(userRegisterRequest);
//            if (response.getSuccess()) {
//                userQueryResponse = userFacadeService.query(userQueryRequest);
//                userInfo = userQueryResponse.getData();
//                StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
//                        .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
//                StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
//                LoginVO loginVO = new LoginVO(userInfo);
//                return Result.success(loginVO);
//            }
//
//            return Result.error(response.getResponseCode(), response.getResponseMessage());
//        } else {
//            //登录
//            StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
//                    .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
//            StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
//            LoginVO loginVO = new LoginVO(userInfo);
//            return Result.success(loginVO);
//        }
//    }
//
//    @PostMapping("/logout")
//    public Result<Boolean> logout() {
//        StpUtil.logout();
//        return Result.success(true);
//    }
//
//    @RequestMapping("test")
//    public String test() {
//        return "test";
//    }
//}
