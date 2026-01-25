package cn.com.mz.app.finance.starter.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RedisUtils 单元测试
 *
 * @author mz
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Redis工具类测试")
class RedisUtilsTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisUtils redisUtils;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // ==================== set 测试 ====================

    @Test
    @DisplayName("测试设置键值对 - 无过期时间")
    void testSetWithoutTimeout() {
        String key = "test:key";
        String value = "testValue";

        redisUtils.set(key, value);

        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    @DisplayName("测试设置键值对 - 带过期时间（秒）")
    void testSetWithTimeoutSeconds() {
        String key = "test:key";
        String value = "testValue";
        long timeout = 60;

        redisUtils.set(key, value, timeout, TimeUnit.SECONDS);

        verify(valueOperations, times(1)).set(key, value, timeout, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试设置键值对 - 带过期时间（分钟）")
    void testSetWithTimeoutMinutes() {
        String key = "test:key";
        String value = "testValue";
        long timeout = 5;

        redisUtils.set(key, value, timeout, TimeUnit.MINUTES);

        verify(valueOperations, times(1)).set(key, value, timeout, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("测试设置键值对 - timeUnit为null时默认使用秒")
    void testSetWithNullTimeUnit() {
        String key = "test:key";
        String value = "testValue";
        long timeout = 60;

        redisUtils.set(key, value, timeout, null);

        verify(valueOperations, times(1)).set(key, value, timeout, TimeUnit.SECONDS);
    }

    // ==================== get 测试 ====================

    @Test
    @DisplayName("测试获取值 - 键存在")
    void testGetKeyExists() {
        String key = "test:key";
        String expectedValue = "testValue";

        when(valueOperations.get(key)).thenReturn(expectedValue);

        String result = redisUtils.get(key);

        assertEquals(expectedValue, result);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("测试获取值 - 键不存在")
    void testGetKeyNotExists() {
        String key = "test:notexists";

        when(valueOperations.get(key)).thenReturn(null);

        String result = redisUtils.get(key);

        assertNull(result);
        verify(valueOperations, times(1)).get(key);
    }

    // ==================== delete 测试 ====================

    @Test
    @DisplayName("测试删除键")
    void testDelete() {
        String key = "test:key";

        redisUtils.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }

    // ==================== exists 测试 ====================

    @Test
    @DisplayName("测试检查键是否存在 - 存在")
    void testExistsKeyExists() {
        String key = "test:key";

        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = redisUtils.exists(key);

        assertTrue(result);
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("测试检查键是否存在 - 不存在")
    void testExistsKeyNotExists() {
        String key = "test:notexists";

        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean result = redisUtils.exists(key);

        assertFalse(result);
        verify(redisTemplate, times(1)).hasKey(key);
    }

    // ==================== expire 测试 ====================

    @Test
    @DisplayName("测试设置过期时间 - 秒")
    void testExpireSeconds() {
        String key = "test:key";
        long timeout = 60;

        redisUtils.expire(key, timeout, TimeUnit.SECONDS);

        verify(redisTemplate, times(1)).expire(key, timeout, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试设置过期时间 - 分钟")
    void testExpireMinutes() {
        String key = "test:key";
        long timeout = 5;

        redisUtils.expire(key, timeout, TimeUnit.MINUTES);

        verify(redisTemplate, times(1)).expire(key, timeout, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("测试设置过期时间 - timeUnit为null时默认使用秒")
    void testExpireNullTimeUnit() {
        String key = "test:key";
        long timeout = 60;

        redisUtils.expire(key, timeout, null);

        verify(redisTemplate, times(1)).expire(key, timeout, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试设置过期时间 - Duration")
    void testExpireWithDuration() {
        String key = "test:key";
        Duration duration = Duration.ofMinutes(5);

        redisUtils.expire(key, duration);

        verify(redisTemplate, times(1)).expire(key, duration);
    }

    // ==================== getExpire 测试 ====================

    @Test
    @DisplayName("测试获取过期时间")
    void testGetExpire() {
        String key = "test:key";
        long expectedExpire = 300L; // 5分钟

        when(redisTemplate.getExpire(key)).thenReturn(expectedExpire);

        long result = redisUtils.getExpire(key);

        assertEquals(expectedExpire, result);
        verify(redisTemplate, times(1)).getExpire(key);
    }

    // ==================== setIfAbsent 测试 ====================

    @Test
    @DisplayName("测试设置键值对（仅当键不存在） - 成功")
    void testSetIfAbsentSuccess() {
        String key = "test:key";
        String value = "testValue";

        when(valueOperations.setIfAbsent(key, value)).thenReturn(true);

        Boolean result = redisUtils.setIfAbsent(key, value);

        assertTrue(result);
        verify(valueOperations, times(1)).setIfAbsent(key, value);
    }

    @Test
    @DisplayName("测试设置键值对（仅当键不存在） - 失败（键已存在）")
    void testSetIfAbsentFailed() {
        String key = "test:key";
        String value = "testValue";

        when(valueOperations.setIfAbsent(key, value)).thenReturn(false);

        Boolean result = redisUtils.setIfAbsent(key, value);

        assertFalse(result);
        verify(valueOperations, times(1)).setIfAbsent(key, value);
    }

    // ==================== incr 测试 ====================

    @Test
    @DisplayName("测试值自增")
    void testIncr() {
        String key = "test:counter";
        long expectedValue = 1L;

        when(valueOperations.increment(key)).thenReturn(expectedValue);

        Long result = redisUtils.incr(key);

        assertEquals(expectedValue, result);
        verify(valueOperations, times(1)).increment(key);
    }

    @Test
    @DisplayName("测试值自增 - 多次调用")
    void testIncrMultipleTimes() {
        String key = "test:counter";

        when(valueOperations.increment(key)).thenReturn(1L, 2L, 3L);

        assertEquals(1L, redisUtils.incr(key));
        assertEquals(2L, redisUtils.incr(key));
        assertEquals(3L, redisUtils.incr(key));

        verify(valueOperations, times(3)).increment(key);
    }

    // ==================== 组合测试 ====================

    @Test
    @DisplayName("测试设置并获取值")
    void testSetAndGet() {
        String key = "test:key";
        String value = "testValue";

        redisUtils.set(key, value);
        when(valueOperations.get(key)).thenReturn(value);

        String result = redisUtils.get(key);

        assertEquals(value, result);
        verify(valueOperations, times(1)).set(key, value);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("测试设置带过期时间的键并检查")
    void testSetWithExpireAndExists() {
        String key = "test:key";
        String value = "testValue";

        redisUtils.set(key, value, 60, TimeUnit.SECONDS);
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean exists = redisUtils.exists(key);

        assertTrue(exists);
        verify(valueOperations, times(1)).set(key, value, 60, TimeUnit.SECONDS);
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    @DisplayName("测试删除不存在的键")
    void testDeleteNotExistsKey() {
        String key = "test:notexists";

        // 删除不存在的键不应该抛出异常
        assertDoesNotThrow(() -> {
            redisUtils.delete(key);
        });

        verify(redisTemplate, times(1)).delete(key);
    }

    @Test
    @DisplayName("测试获取不存在键的过期时间")
    void testGetExpireNotExistsKey() {
        String key = "test:notexists";

        when(redisTemplate.getExpire(key)).thenReturn(-2L); // Redis返回-2表示键不存在

        long result = redisUtils.getExpire(key);

        assertEquals(-2L, result);
        verify(redisTemplate, times(1)).getExpire(key);
    }

    // ==================== 边界测试 ====================

    @Test
    @DisplayName("测试设置空字符串值")
    void testSetEmptyStringValue() {
        String key = "test:key";
        String value = "";

        redisUtils.set(key, value);

        verify(valueOperations, times(1)).set(key, value);
    }

    @Test
    @DisplayName("测试设置零过期时间")
    void testSetWithZeroTimeout() {
        String key = "test:key";
        String value = "testValue";

        redisUtils.set(key, value, 0, TimeUnit.SECONDS);

        verify(valueOperations, times(1)).set(key, value, 0, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试设置负数过期时间")
    void testSetWithNegativeTimeout() {
        String key = "test:key";
        String value = "testValue";

        redisUtils.set(key, value, -1, TimeUnit.SECONDS);

        verify(valueOperations, times(1)).set(key, value, -1, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("测试使用不同时间单位设置过期时间")
    void testExpireWithDifferentTimeUnits() {
        String key = "test:key";

        // 毫秒
        redisUtils.expire(key, 1000, TimeUnit.MILLISECONDS);
        verify(redisTemplate, times(1)).expire(key, 1000, TimeUnit.MILLISECONDS);

        // 秒
        redisUtils.expire(key, 60, TimeUnit.SECONDS);
        verify(redisTemplate, times(1)).expire(key, 60, TimeUnit.SECONDS);

        // 分钟
        redisUtils.expire(key, 10, TimeUnit.MINUTES);
        verify(redisTemplate, times(1)).expire(key, 10, TimeUnit.MINUTES);

        // 小时
        redisUtils.expire(key, 2, TimeUnit.HOURS);
        verify(redisTemplate, times(1)).expire(key, 2, TimeUnit.HOURS);

        // 天
        redisUtils.expire(key, 1, TimeUnit.DAYS);
        verify(redisTemplate, times(1)).expire(key, 1, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("测试setIfAbsent返回null")
    void testSetIfAbsentReturnsNull() {
        String key = "test:key";
        String value = "testValue";

        when(valueOperations.setIfAbsent(key, value)).thenReturn(null);

        Boolean result = redisUtils.setIfAbsent(key, value);

        assertNull(result);
        verify(valueOperations, times(1)).setIfAbsent(key, value);
    }
}
