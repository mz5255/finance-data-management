package cn.com.mz.app.config;

import cn.com.mz.app.SmsService;
import cn.com.mz.app.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author mz
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfiguration {

    @Autowired
    private SmsProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @Profile({"default","prod"})
    public SmsService smsService() {
        SmsServiceImpl smsService = new SmsServiceImpl();
        smsService.setHost(properties.getHost());
        smsService.setPath(properties.getPath());
        smsService.setAppcode(properties.getAppcode());
        smsService.setSmsSignId(properties.getSmsSignId());
        smsService.setTemplateId(properties.getTemplateId());
        return smsService;
    }

}
