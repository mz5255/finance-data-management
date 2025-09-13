package cn.com.mz.app.finance.starter.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 *
 * @author mz
 */
@Configuration
@EnableMethodCache(basePackages = "cn.com.mz.app.finance")
public class CacheConfiguration {
}
