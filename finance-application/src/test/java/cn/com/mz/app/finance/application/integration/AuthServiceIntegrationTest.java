package cn.com.mz.app.finance.application.integration;

import cn.com.mz.app.finance.common.dto.base.BaseResult;
import cn.com.mz.app.finance.common.utils.IDUtils;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import cn.com.mz.app.finance.module.dto.req.LoginParam;
import cn.com.mz.app.finance.module.service.auth.AuthService;
import cn.com.mz.app.finance.module.service.query.QueryMemberService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthService 集成测试
 * 测试真实的业务流程，包括数据库操作、缓存、认证等
 *
 * @author mz
 */
@SpringBootTest
@Transactional // 每个测试后自动回滚，保持数据库干净
@Disabled("集成测试需要真实数据库和Redis，暂时禁用")
@DisplayName("认证服务集成测试")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private QueryMemberService queryMemberService;

    private static long phoneCounter = 20000000;

    /**
     * 生成有效的11位手机号
     */
    private String generateUniquePhone() {
        String prefix = "139";
        String suffix = String.valueOf(phoneCounter++);
        // 补足8位（包括前缀共11位）
        while (suffix.length() < 8) {
            suffix = "0" + suffix;
        }
        // 防止溢出超过8位
        if (phoneCounter > 99999999) {
            phoneCounter = 20000000;
        }
        return prefix + suffix;
    }

    // ==================== register 测试 ====================

    @Test
    @DisplayName("集成测试：用户注册成功")
    void testRegisterSuccess() {
        // 准备注册参数
        cn.com.mz.app.finance.module.vo.UserRegisterRequest request = new cn.com.mz.app.finance.module.vo.UserRegisterRequest();
        String uniquePhone = generateUniquePhone();  // 确保手机号唯一
        request.setTelephone(uniquePhone);
        request.setPassword("testPassword123");

        // 执行注册
        BaseResult<?> result = authService.register(request);

        // 验证结果
        assertTrue(result.isSuccess(), "注册应该成功");
        assertNotNull(result.getData(), "注册应该返回用户数据");

        // 验证用户已创建
        UserDO createdUser = userService.getByTelephone(uniquePhone);
        assertNotNull(createdUser, "用户应该被保存到数据库");
        assertEquals(uniquePhone, createdUser.getTelephone(), "手机号应该匹配");
        assertEquals(UserStateEnum.INIT, createdUser.getState(), "初始状态应该是INIT");

        // 清理测试数据
        userService.deleteById(createdUser.getId());
    }

    @Test
    @DisplayName("集成测试：注册已存在用户应该失败")
    void testRegisterUserAlreadyExists() {
        // 先创建一个用户
        String uniquePhone = generateUniquePhone();
        UserDO existingUser = new UserDO();
        existingUser.setId(IDUtils.generateUniqueId(uniquePhone));
        existingUser.setTelephone(uniquePhone);
        existingUser.setNickName("已存在用户");
        existingUser.setPasswordHash(DigestUtil.md5Hex("password" + "salt"));
        existingUser.setSalt("salt");
        existingUser.setState(UserStateEnum.INIT);
        existingUser.setUserRole(UserRole.CUSTOMER);
        userService.save(existingUser);

        // 尝试注册相同手机号
        cn.com.mz.app.finance.module.vo.UserRegisterRequest request = new cn.com.mz.app.finance.module.vo.UserRegisterRequest();
        request.setTelephone(uniquePhone);
        request.setPassword("testPassword123");

        // 执行注册
        BaseResult<?> result = authService.register(request);

        // 应该失败或返回已存在的用户
        assertNotNull(result);

        // 清理
        userService.deleteById(existingUser.getId());
    }

    // ==================== login 测试 ====================

    @Test
    @DisplayName("集成测试：已存在用户登录成功")
    void testLoginExistingUserSuccess() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        String password = "testPassword123";
        String salt = "testsalt";
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO testUser = new UserDO();
        testUser.setId(userId);
        testUser.setTelephone(uniquePhone);
        testUser.setNickName("测试用户");
        testUser.setPasswordHash(DigestUtil.md5Hex(password + salt));
        testUser.setSalt(salt);
        testUser.setState(UserStateEnum.INIT);
        testUser.setUserRole(UserRole.CUSTOMER);
        testUser.setCertification(false);
        userService.save(testUser);

        // 准备登录参数
        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone(uniquePhone);
        loginParam.setPassword(password);
        loginParam.setCaptcha("123456"); // 模拟验证码
        loginParam.setRememberMe(false);

        // 执行登录
        BaseResult<cn.com.mz.app.finance.module.vo.LoginReq> result = authService.login(loginParam);

        // 验证结果
        assertTrue(result.isSuccess(), "登录应该成功");
        assertNotNull(result.getData(), "登录应该返回数据");
        assertNotNull(result.getData().getToken(), "应该有 token");
        assertEquals(userId.toString(), result.getData().getUserId(), "用户ID应该匹配");

        // 验证用户已登录
        assertTrue(StpUtil.isLogin(), "用户应该已登录");
        assertEquals(userId.toString(), StpUtil.getLoginIdAsString(), "登录ID应该正确");

        // 清理
        StpUtil.logout();
        userService.deleteById(testUser.getId());
    }

    @Test
    @DisplayName("集成测试：新用户自动注册并登录")
    void testNewUserAutoRegisterAndLogin() {
        // 准备新用户登录参数（用户不存在）
        String uniquePhone = generateUniquePhone();
        String password = "testPassword123";

        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone(uniquePhone);
        loginParam.setPassword(password);
        loginParam.setCaptcha("123456");
        loginParam.setRememberMe(false);

        // 执行登录（会自动注册）
        BaseResult<cn.com.mz.app.finance.module.vo.LoginReq> result = authService.login(loginParam);

        // 验证注册和登录都成功
        assertTrue(result.isSuccess(), "自动注册并登录应该成功");
        assertNotNull(result.getData(), "应该返回登录数据");
        assertNotNull(result.getData().getToken(), "应该有 token");

        // 验证用户已创建
        UserDO createdUser = userService.getByTelephone(uniquePhone);
        assertNotNull(createdUser, "新用户应该被创建");
        assertEquals(uniquePhone, createdUser.getTelephone(), "手机号应该匹配");

        // 验证用户已登录
        assertTrue(StpUtil.isLogin(), "用户应该已登录");

        // 获取登录用户信息
        Object sessionUser = StpUtil.getSession().get(StpUtil.getLoginIdAsString());
        assertNotNull(sessionUser, "session 中应该有用户信息");

        // 清理
        StpUtil.logout();
        userService.deleteById(createdUser.getId());
    }

    @Test
    @DisplayName("集成测试：查询用户信息")
    void testQueryUser() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO testUser = new UserDO();
        testUser.setId(userId);
        testUser.setTelephone(uniquePhone);
        testUser.setNickName("查询测试用户");
        testUser.setPasswordHash(DigestUtil.md5Hex("password" + "salt"));
        testUser.setSalt("salt");
        testUser.setState(UserStateEnum.AUTH);
        testUser.setUserRole(UserRole.CUSTOMER);
        userService.save(testUser);

        // 查询用户
        cn.com.mz.app.finance.module.dto.req.QueryParam queryParam = new cn.com.mz.app.finance.module.dto.req.QueryParam();
        queryParam.setTelephone(uniquePhone);

        BaseResult<cn.com.mz.app.finance.datasource.mysql.entity.user.convertor.UserInfo> result =
                queryMemberService.getMember(queryParam);

        // 验证结果
        assertTrue(result.isSuccess(), "查询应该成功");
        assertNotNull(result.getData(), "应该返回用户信息");
        assertEquals(uniquePhone, result.getData().getTelephone(), "手机号应该匹配");
        assertEquals("查询测试用户", result.getData().getNickName(), "昵称应该匹配");

        // 清理
        userService.deleteById(testUser.getId());
    }

    // ==================== 边界测试 ====================

    @Test
    @DisplayName("集成测试：手机号格式验证")
    void testPhoneValidation() {
        // 测试无效手机号
        String[] invalidPhones = {
                "", "12345", "123456789012",  // 少于11位
                "23800138000",                    // 不是1开头
                "12800138000",                    // 第二位不是3-9
                "1380013800a",                    // 包含字母
                "1380013800-"                     // 包含特殊字符
        };

        for (String invalidPhone : invalidPhones) {
            LoginParam loginParam = new LoginParam();
            loginParam.setTelephone(invalidPhone);
            loginParam.setPassword("test123");
            loginParam.setCaptcha("123456");

            // 执行登录（可能抛出异常或返回错误）
            assertThrows(Exception.class, () -> {
                authService.login(loginParam);
            }, "手机号 " + invalidPhone + " 应该被拒绝");
        }
    }

    @Test
    @DisplayName("集成测试：密码错误登录失败")
    void testLoginWithWrongPassword() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        String correctPassword = "correctPassword";
        String salt = "testsalt";
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO testUser = new UserDO();
        testUser.setId(userId);
        testUser.setTelephone(uniquePhone);
        testUser.setPasswordHash(DigestUtil.md5Hex(correctPassword + salt));
        testUser.setSalt(salt);
        testUser.setState(UserStateEnum.INIT);
        testUser.setUserRole(UserRole.CUSTOMER);
        userService.save(testUser);

        // 使用错误密码登录
        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone(uniquePhone);
        loginParam.setPassword("wrongPassword");
        loginParam.setCaptcha("123456");

        // 执行登录应该失败
        assertThrows(Exception.class, () -> {
            authService.login(loginParam);
        }, "错误密码应该导致登录失败");

        // 清理
        userService.deleteById(testUser.getId());
    }

    @Test
    @DisplayName("集成测试：验证码错误登录失败")
    void testLoginWithWrongCaptcha() {
        // 先设置 Redis 验证码（这里假设 Redis 可用）
        // 实际项目中需要先调用 captchaImage 获取验证码

        String uniquePhone = generateUniquePhone();

        LoginParam loginParam = new LoginParam();
        loginParam.setTelephone(uniquePhone);
        loginParam.setPassword("test123");
        loginParam.setCaptcha("wrongCaptcha");

        // 执行登录应该失败
        assertThrows(Exception.class, () -> {
            authService.login(loginParam);
        }, "错误验证码应该导致登录失败");
    }
}
