package cn.com.mz.app.finance.ai.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 模块配置
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModuleConfig {

    /**
     * 配置类型
     */
    private String type;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 最大上下文轮数
     */
    @Builder.Default
    private int maxContextRounds = 20;

    /**
     * 温度参数
     */
    @Builder.Default
    private double temperature = 0.7;

    /**
     * 最大Token数
     */
    @Builder.Default
    private int maxTokens = 4096;

    /**
     * 超时时间（毫秒）
     */
    @Builder.Default
    private long timeout = 30000;

    /**
     * 是否启用流式输出
     */
    @Builder.Default
    private boolean streamEnabled = true;

    /**
     * 扩展配置
     */
    @Builder.Default
    private Map<String, Object> extraConfig = new HashMap<>();

    /**
     * 创建默认配置
     */
    public static AiModuleConfig defaultConfig() {
        return AiModuleConfig.builder()
                .type("default")
                .modelId("zhipu")
                .maxContextRounds(20)
                .temperature(0.7)
                .maxTokens(4096)
                .timeout(30000)
                .streamEnabled(true)
                .build();
    }

    /**
     * 获取扩展配置
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtraConfig(String key, Class<T> clazz) {
        Object value = extraConfig.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 设置扩展配置
     */
    public AiModuleConfig putExtraConfig(String key, Object value) {
        if (this.extraConfig == null) {
            this.extraConfig = new HashMap<>();
        }
        this.extraConfig.put(key, value);
        return this;
    }
}
