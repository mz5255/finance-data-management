package cn.com.mz.app.finance.application.integration;

import cn.com.mz.app.finance.common.utils.IDUtils;
import cn.com.mz.app.finance.datasource.mysql.entity.user.UserDO;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserRole;
import cn.com.mz.app.finance.datasource.mysql.entity.user.enums.UserStateEnum;
import cn.com.mz.app.finance.datasource.mysql.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService 集成测试
 * 测试真实的数据库操作和 MyBatis-Plus Lambda 表达式
 *
 * @author mz
 */
@SpringBootTest
@Transactional
@Disabled("集成测试需要真实数据库，暂时禁用")
@DisplayName("用户服务集成测试")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private static long phoneCounter = 10000000;

    /**
     * 生成有效的11位手机号
     */
    private String generateUniquePhone() {
        String prefix = "138";
        String suffix = String.valueOf(phoneCounter++);
        // 补足8位（包括前缀共11位）
        while (suffix.length() < 8) {
            suffix = "0" + suffix;
        }
        // 防止溢出超过8位
        if (phoneCounter > 99999999) {
            phoneCounter = 10000000;
        }
        return prefix + suffix;
    }

    // ==================== create 测试 ====================

    @Test
    @DisplayName("集成测试：创建新用户")
    void testCreateUser() {
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("集成测试用户");
        user.setPasswordHash("hashedPassword");
        user.setSalt("salt");
        user.setState(UserStateEnum.INIT);
        user.setUserRole(UserRole.CUSTOMER);
        user.setCertification(false);

        boolean result = userService.save(user);

        assertTrue(result, "保存用户应该成功");
        assertNotNull(user.getId(), "用户ID应该被设置");

        // 验证用户可以查询到
        UserDO savedUser = userService.getById(userId);
        assertNotNull(savedUser, "用户应该能被查询到");
        assertEquals(uniquePhone, savedUser.getTelephone(), "手机号应该匹配");

        // 清理
        userService.deleteById(userId);
    }

    @Test
    @DisplayName("集成测试：根据手机号查询用户")
    void testGetByTelephone() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("手机号查询测试");
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.AUTH);
        user.setUserRole(UserRole.ADMIN);
        userService.save(user);

        // 查询用户
        UserDO foundUser = userService.getByTelephone(uniquePhone);

        assertNotNull(foundUser, "应该能找到用户");
        assertEquals(uniquePhone, foundUser.getTelephone(), "手机号应该匹配");
        assertEquals("手机号查询测试", foundUser.getNickName(), "昵称应该匹配");

        // 清理
        userService.deleteById(userId);
    }

    @Test
    @DisplayName("集成测试：根据ID查询用户")
    void testGetById() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("ID查询测试");
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.FROZEN);
        user.setUserRole(UserRole.SUPERADMIN);
        userService.save(user);

        // 根据 ID 查询
        UserDO foundUser = userService.getById(userId);

        assertNotNull(foundUser, "应该能找到用户");
        assertEquals(userId, foundUser.getId(), "用户ID应该匹配");

        // 清理
        userService.deleteById(userId);
    }

    // ==================== update 测试 ====================

    @Test
    @DisplayName("集成测试：更新用户状态")
    void testUpdateUserState() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("状态更新测试");
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.INIT);
        user.setUserRole(UserRole.CUSTOMER);
        userService.save(user);

        // 更新用户状态
        boolean result = userService.updateStateById(userId, UserStateEnum.AUTH);

        assertTrue(result, "更新状态应该成功");

        // 验证状态已更新
        UserDO updatedUser = userService.getById(userId);
        assertEquals(UserStateEnum.AUTH, updatedUser.getState(), "状态应该被更新");

        // 清理
        userService.deleteById(userId);
    }

    @Test
    @DisplayName("集成测试：更新最后登录时间")
    void testUpdateLastLoginTime() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("登录时间测试");
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.INIT);
        user.setUserRole(UserRole.CUSTOMER);
        userService.save(user);

        // 先记录当前最后登录时间
        UserDO beforeUpdate = userService.getById(userId);
        LocalTime beforeTime = beforeUpdate.getLastLoginTime();

        // 更新最后登录时间
        boolean result = userService.updateLastLoginTime(userId);

        assertTrue(result, "更新登录时间应该成功");

        // 验证时间已更新
        UserDO afterUpdate = userService.getById(userId);
        assertNotNull(afterUpdate.getLastLoginTime(), "最后登录时间应该被设置");

        // 清理
        userService.deleteById(userId);
    }

    @Test
    @DisplayName("集成测试：根据昵称查询用户")
    void testGetByNikeName() {
        // 创建测试用户
        String uniqueNickName = "昵称测试" + System.currentTimeMillis();
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName(uniqueNickName);
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.AUTH);
        user.setUserRole(UserRole.CUSTOMER);
        userService.save(user);

        // 根据昵称查询
        UserDO foundUser = userService.getByNikeName(uniqueNickName);

        assertNotNull(foundUser, "应该能找到用户");
        assertEquals(uniqueNickName, foundUser.getNickName(), "昵称应该匹配");

        // 清理
        userService.deleteById(userId);
    }

    // ==================== query 测试 ====================

    @Test
    @DisplayName("集成测试：根据状态查询用户列表")
    void testGetByState() {
        UserStateEnum targetState = UserStateEnum.AUTH;

        // 创建多个不同状态的用户
        for (int i = 0; i < 5; i++) {
            String uniquePhone = generateUniquePhone();
            Long userId = IDUtils.generateUniqueId(uniquePhone);

            UserDO user = new UserDO();
            user.setId(userId);
            user.setTelephone(uniquePhone);
            user.setNickName("状态查询测试" + i);
            user.setPasswordHash("hash");
            user.setSalt("salt");
            user.setState(targetState);
            user.setUserRole(UserRole.CUSTOMER);
            userService.save(user);
        }

        // 根据状态查询
        List<UserDO> users = userService.getByState(targetState);

        assertNotNull(users, "应该能查询到用户列表");
        assertTrue(users.size() >= 5, "至少应该有5个用户");

        // 验证所有返回的用户都是目标状态
        for (UserDO user : users) {
            assertEquals(targetState, user.getState(), "所有用户应该是目标状态");
        }

        // 清理
        for (UserDO user : users) {
            userService.deleteById(user.getId());
        }
    }

    @Test
    @DisplayName("集成测试：根据角色查询用户列表")
    void testGetByUserRole() {
        UserRole targetRole = UserRole.ADMIN;

        // 创建多个不同角色的用户
        for (int i = 0; i < 5; i++) {
            String uniquePhone = generateUniquePhone();
            Long userId = IDUtils.generateUniqueId(uniquePhone);

            UserDO user = new UserDO();
            user.setId(userId);
            user.setTelephone(uniquePhone);
            user.setNickName("角色查询测试" + i);
            user.setPasswordHash("hash");
            user.setSalt("salt");
            user.setState(UserStateEnum.INIT);
            user.setUserRole(targetRole);
            userService.save(user);
        }

        // 根据角色查询
        List<UserDO> users = userService.getByUserRole(targetRole);

        assertNotNull(users, "应该能查询到用户列表");
        assertTrue(users.size() >= 5, "至少应该有5个用户");

        // 验证所有返回的用户都是目标角色
        for (UserDO user : users) {
            assertEquals(targetRole, user.getUserRole(), "所有用户应该是目标角色");
        }

        // 清理
        for (UserDO user : users) {
            userService.deleteById(user.getId());
        }
    }

    // ==================== delete 测试 ====================

    @Test
    @DisplayName("集成测试：删除用户")
    void testDeleteUser() {
        // 创建测试用户
        String uniquePhone = generateUniquePhone();
        Long userId = IDUtils.generateUniqueId(uniquePhone);

        UserDO user = new UserDO();
        user.setId(userId);
        user.setTelephone(uniquePhone);
        user.setNickName("删除测试用户");
        user.setPasswordHash("hash");
        user.setSalt("salt");
        user.setState(UserStateEnum.INIT);
        user.setUserRole(UserRole.CUSTOMER);
        userService.save(user);

        // 验证用户存在
        assertNotNull(userService.getById(userId), "用户应该存在");

        // 删除用户
        boolean result = userService.deleteById(userId);

        assertTrue(result, "删除应该成功");

        // 验证用户已删除
        UserDO deletedUser = userService.getById(userId);
        assertNull(deletedUser, "用户应该被删除");
    }

    // ==================== count 测试 ====================

    @Test
    @DisplayName("集成测试：统计用户总数")
    void testCountUsers() {
        // 获取初始用户数
        long initialCount = userService.countUsers();

        // 创建几个测试用户并记录ID
        java.util.List<Long> createdUserIds = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String uniquePhone = generateUniquePhone();
            Long userId = IDUtils.generateUniqueId(uniquePhone);

            UserDO user = new UserDO();
            user.setId(userId);
            user.setTelephone(uniquePhone);
            user.setNickName("统计测试用户" + i);
            user.setPasswordHash("hash");
            user.setSalt("salt");
            user.setState(UserStateEnum.INIT);
            user.setUserRole(UserRole.CUSTOMER);
            userService.save(user);
            createdUserIds.add(userId);
        }

        // 验证用户数增加
        long newCount = userService.countUsers();
        assertEquals(initialCount + 3, newCount, "用户数应该增加3个");

        // 清理
        for (Long userId : createdUserIds) {
            userService.deleteById(userId);
        }
    }

    @Test
    @DisplayName("集成测试：统计已认证用户数")
    void testCountCertifiedUsers() {
        // 创建一些已认证用户并记录ID
        java.util.List<Long> createdUserIds = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String uniquePhone = generateUniquePhone();
            Long userId = IDUtils.generateUniqueId(uniquePhone);

            UserDO user = new UserDO();
            user.setId(userId);
            user.setTelephone(uniquePhone);
            user.setNickName("认证测试用户" + i);
            user.setPasswordHash("hash");
            user.setSalt("salt");
            user.setState(UserStateEnum.AUTH);
            user.setUserRole(UserRole.CUSTOMER);
            user.setCertification(true);  // 已认证
            userService.save(user);
            createdUserIds.add(userId);
        }

        // 统计已认证用户
        long certifiedCount = userService.countCertifiedUsers();

        // 验证至少有我们创建的3个已认证用户
        assertTrue(certifiedCount >= 3, "应该至少有3个已认证用户");

        // 清理
        for (Long userId : createdUserIds) {
            userService.deleteById(userId);
        }
    }

    // ==================== 边界测试 ====================

    @Test
    @DisplayName("集成测试：查询不存在的用户")
    void testGetByIdNotExists() {
        Long nonExistentUserId = 999999999999L;

        UserDO user = userService.getById(nonExistentUserId);

        assertNull(user, "查询不存在的用户应该返回 null");
    }

    @Test
    @DisplayName("集成测试：删除不存在的用户")
    void testDeleteNotExists() {
        Long nonExistentUserId = 999999999999L;

        boolean result = userService.deleteById(nonExistentUserId);

        assertFalse(result, "删除不存在的用户应该返回 false");
    }

    @Test
    @DisplayName("集成测试：更新不存在的用户")
    void testUpdateNotExists() {
        Long nonExistentUserId = 999999999999L;

        boolean result = userService.updateStateById(nonExistentUserId, UserStateEnum.AUTH);

        assertFalse(result, "更新不存在的用户应该返回 false");
    }
}
