package cn.com.mz.app.finance.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 配置类
 * 解决 JavaScript Long 类型精度丢失问题和 Java 8 日期时间类型序列化问题
 *
 * @author mz
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 注册 Java 8 日期时间模块
        objectMapper.registerModule(new JavaTimeModule());

        // 配置 Long 类型序列化为 String，避免 JavaScript 精度丢失
        SimpleModule longModule = new SimpleModule();
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(longModule);

        return objectMapper;
    }
}
