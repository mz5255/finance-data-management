package cn.com.mz.app.finance.starter.config;

/**
 * @author 马震
 * @version 1.0
 * @date 2026/1/19 12:02
 */

public class RedisKey {
    private final static String PREFIX = "FINANCE:";

    /**
     * 业务主键生成器
     */
    public final static  String UNIQUE_ID = PREFIX + "UNIQUE_ID";
}
