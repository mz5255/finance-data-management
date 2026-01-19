package cn.com.mz.app.finance.starter.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author 马震
 * @version 1.0
 * @date 2026/1/7 10:56
 */
@Service
public class RedisUtils {
    @Resource
    private StringRedisTemplate redisTemplate;

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        if (timeUnit == null){
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }
    }
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    public void expire(String key, long timeout, TimeUnit timeUnit) {
        if (timeUnit == null){
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } else {
            redisTemplate.expire(key, timeout, timeUnit);
        }
    }
    public void expire(String key, Duration duration){
        redisTemplate.expire(key, duration);
    }

    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
    public Boolean setIfAbsent(String key, String value) {
       return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Long incr(String key){
      return redisTemplate.opsForValue().increment(key);
    }
}
