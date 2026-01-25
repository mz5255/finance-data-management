package cn.com.mz.app.finance.application.webapi.system.user;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.exceptions.BusinessException;
import cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.dto.req.QueryParam;
import cn.com.mz.app.finance.module.dto.req.RegisterParam;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.service.query.QueryMemberService;
import cn.com.mz.app.finance.module.vo.LoginReq;
import cn.com.mz.app.finance.module.vo.UserRegisterRequest;
import cn.com.mz.app.finance.starter.utils.RedisUtils;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 单元测试
 *
 * @author mz
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Mock
    private RedisUtils redisUtils;

    @Mock
    private AuthService authService;

    @Mock
    private QueryMemberService queryMemberService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    // ==================== captchaImage 测试 ====================

    @Test
    @DisplayName("测试获取验证码接口 - 成功")
    void testCaptchaImageSuccess() throws Exception {
        // captchaImage 方法直接输出图片，不返回数据
        doNothing().when(authService).captchaImage(anyString());

        mockMvc.perform(get("/api/finance-data/auth/captchaImage")
                        .param("telephone", "13800138000"))
                .andExpect(status().isOk());

        verify(authService, times(1)).captchaImage("13800138000");
    }

    // ==================== register 测试 ====================

    @Test
    @DisplayName("测试用户注册接口 - 验证码错误")
    void testRegisterInvalidCaptcha() throws Exception {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setTelephone("13800138000");
        registerParam.setCaptcha("wrong");
        registerParam.setPassword("test123");

        when(redisUtils.get(anyString())).thenReturn("correct");

        // 由于验证码在Controller层校验，会抛出异常
        assertThrows(BusinessException.class, () -> {
            authController.register(registerParam);
        });

        verify(authService, never()).register(any());
    }

    @Test
    @DisplayName("测试用户注册接口 - 成功")
    void testRegisterSuccess() throws Exception {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setTelephone("13900139000");
        registerParam.setCaptcha("123456");
        registerParam.setPassword("test123");

        when(redisUtils.get(anyString())).thenReturn("123456");
        when(authService.register(any(UserRegisterRequest.class)))
                .thenReturn((BaseResult) BaseResult.success(true));

        BaseResult<Boolean> result = authController.register(registerParam);

        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        verify(authService, times(1)).register(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("测试用户注册接口 - 服务层失败")
    void testRegisterServiceFailed() throws Exception {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setTelephone("13900139000");
        registerParam.setCaptcha("123456");
        registerParam.setPassword("test123");

        when(redisUtils.get(anyString())).thenReturn("123456");
        when(authService.register(any(UserRegisterRequest.class)))
                .thenReturn((BaseResult) BaseResult.error(500, "注册失败"));

        BaseResult<Boolean> result = authController.register(registerParam);

        assertFalse(result.isSuccess());
        verify(authService, times(1)).register(any(UserRegisterRequest.class));
    }

    // ==================== login 测试 ====================

    @Test
    @DisplayName("测试用户登录接口 - 成功")
    void testLoginSuccess() {
        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone("13800138000");
        loginParam.setCaptcha("123456");
        loginParam.setPassword("test123");
        loginParam.setRememberMe(true);

        LoginReq loginReq = new LoginReq();
        loginReq.setUserId("123456789012345678");
        loginReq.setToken("test-token");
        loginReq.setTokenExpiration(System.currentTimeMillis() + 3600000);

        when(authService.login(loginParam))
                .thenReturn(BaseResult.success(loginReq));

        BaseResult<LoginReq> result = authController.login(loginParam);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("123456789012345678", result.getData().getUserId());
        assertEquals("test-token", result.getData().getToken());
        verify(authService, times(1)).login(loginParam);
    }

    @Test
    @DisplayName("测试用户登录接口 - 失败")
    void testLoginFailed() {
        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone("13800138000");
        loginParam.setCaptcha("wrong");
        loginParam.setPassword("test123");

        when(authService.login(loginParam))
                .thenReturn((BaseResult) BaseResult.error(400, "验证码错误"));

        BaseResult<LoginReq> result = authController.login(loginParam);

        assertFalse(result.isSuccess());
        assertEquals("验证码错误", result.getMessage());
        verify(authService, times(1)).login(loginParam);
    }

    // ==================== query 测试 ====================

    @Test
    @DisplayName("测试查询用户信息接口 - 成功")
    void testQuerySuccess() {
        QueryParam queryParam = new QueryParam();
        queryParam.setTelephone("13800138000");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("123456789012345678");
        userInfo.setNickName("测试用户");
        userInfo.setTelephone("13800138000");

        when(queryMemberService.getMember(queryParam))
                .thenReturn(BaseResult.success(userInfo));

        BaseResult<UserInfo> result = authController.query(queryParam);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("测试用户", result.getData().getNickName());
        verify(queryMemberService, times(1)).getMember(queryParam);
    }

    @Test
    @DisplayName("测试查询用户信息接口 - 用户不存在")
    void testQueryUserNotFound() {
        QueryParam queryParam = new QueryParam();
        queryParam.setTelephone("13900139000");

        when(queryMemberService.getMember(queryParam))
                .thenReturn((BaseResult) BaseResult.error(404, "用户不存在"));

        BaseResult<UserInfo> result = authController.query(queryParam);

        assertFalse(result.isSuccess());
        assertEquals("用户不存在", result.getMessage());
        verify(queryMemberService, times(1)).getMember(queryParam);
    }

    // ==================== logout 测试 ====================

    @Test
    @DisplayName("测试用户登出接口 - 成功")
    void testLogoutSuccess() {
        try (MockedStatic<StpUtil> mockedStpUtil = mockStatic(StpUtil.class)) {
            mockedStpUtil.when(StpUtil::logout).thenAnswer(invocation -> null);

            BaseResult<Boolean> result = authController.logout();

            assertTrue(result.isSuccess());
            assertTrue(result.getData());
            mockedStpUtil.verify(StpUtil::logout, times(1));
        }
    }

    // ==================== 参数验证测试 ====================

    @Test
    @DisplayName("测试注册参数验证 - 手机号格式错误")
    void testRegisterParamInvalidPhone() {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setTelephone("12345"); // 无效手机号
        registerParam.setCaptcha("123456");
        registerParam.setPassword("test123");

        // 参数验证通常由@Valid注解处理，这里需要结合实际验证框架
        // 这里仅作为示例，实际需要配合ValidationAutoConfiguration
    }

    @Test
    @DisplayName("测试注册参数验证 - 验证码为空")
    void testRegisterParamEmptyCaptcha() {
        RegisterParam registerParam = new RegisterParam();
        registerParam.setTelephone("13800138000");
        registerParam.setCaptcha(""); // 空验证码
        registerParam.setPassword("test123");

        // 参数验证通常由@Valid注解处理
    }

    // ==================== 辅助测试方法 ====================

    @Test
    @DisplayName("测试控制器路由配置")
    void testControllerMapping() {
        // 验证控制器的RequestMapping配置
        assertEquals("/api/finance-data/auth",
                authController.getClass().getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0]);
    }

    @Test
    @DisplayName("测试Swagger注解配置")
    void testSwaggerAnnotations() {
        // 验证Swagger注解存在
        assertNotNull(authController.getClass().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class));
        assertEquals("权限模块",
                authController.getClass().getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class).name());
    }
}
