package cn.com.mz.app.finance.starter.utils;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static cn.com.mz.app.finance.starter.config.RedisKey.UNIQUE_ID;

/**
 * @author 马震
 * @version 1.0
 * @date 2026/1/19 11:52
 */
@Component
public class UniqueIdUtils {
    @Resource
    private RedisUtils redisUtils;


    /**
     * 可传表名生成唯一key
     * @param key
     * @return
     */
    public String ID(String key) {
        return "FINANCE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + getUniqueId(key+":"+UNIQUE_ID);
    }

    /**
     * 按日期生成每日重置的6位数ID
     *
     * @return 8位数ID
     */
    private String getUniqueId(String key) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String dateKey = key + ":sequence:" + dateStr;
        Long increment = redisUtils.incr(dateKey);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowMidnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT);
        Duration expireDuration = Duration.between(now, tomorrowMidnight);
        redisUtils.expire(dateKey, expireDuration);
        return String.format("%08d", increment);
    }
}
