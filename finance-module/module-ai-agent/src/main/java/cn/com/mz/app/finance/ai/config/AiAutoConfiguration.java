package cn.com.mz.app.finance.ai.config;

import cn.com.mz.app.finance.ai.module.ModuleRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AI Agent 自动配置
 *
 * @author mz
 */
@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties(AiProperties.class)
@ComponentScan(basePackages = "cn.com.mz.app.finance.ai")
@RequiredArgsConstructor
public class AiAutoConfiguration {

    private final AiProperties aiProperties;

    @Bean
    public ModuleRegistry moduleRegistry() {
        ModuleRegistry registry = new ModuleRegistry();
        log.info("AI Module Registry initialized");
        return registry;
    }

    @Bean
    public AiModuleConfigFactory aiModuleConfigFactory() {
        return new AiModuleConfigFactory(aiProperties);
    }
}
