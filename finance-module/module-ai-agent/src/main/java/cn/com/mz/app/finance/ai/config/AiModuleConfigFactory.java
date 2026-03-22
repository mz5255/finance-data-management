package cn.com.mz.app.finance.ai.config;

import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import lombok.RequiredArgsConstructor;

/**
 * AI 模块配置工厂
 *
 * @author mz
 */
@RequiredArgsConstructor
public class AiModuleConfigFactory {

    private final AiProperties aiProperties;

    /**
     * 创建默认配置
     */
    public AiModuleConfig createDefaultConfig() {
        return createConfig(aiProperties.getDefaultAgent());
    }

    /**
     * 根据类型创建配置
     */
    public AiModuleConfig createConfig(String agentType) {
        AiProperties.AgentConfig agentConfig = aiProperties.getAgents().get(agentType);
        String modelId = agentConfig != null && agentConfig.getModel() != null
                ? agentConfig.getModel()
                : aiProperties.getDefaultModel();

        AiProperties.ModelConfig modelConfig = aiProperties.getModels().get(modelId);

        AiModuleConfig.AiModuleConfigBuilder builder = AiModuleConfig.builder()
                .type(agentType)
                .modelId(modelId);

        if (modelConfig != null) {
            builder.maxTokens(modelConfig.getMaxTokens())
                    .temperature(modelConfig.getTemperature())
                    .timeout(modelConfig.getTimeout());
        }

        if (agentConfig != null) {
            builder.maxContextRounds(agentConfig.getMaxContextRounds());
        }

        return builder.build();
    }

    /**
     * 获取模型配置
     */
    public AiProperties.ModelConfig getModelConfig(String modelId) {
        return aiProperties.getModels().get(modelId);
    }

    /**
     * 获取工具配置
     */
    public AiProperties.ToolConfig getToolConfig(String toolName) {
        return aiProperties.getTools().get(toolName);
    }

    /**
     * 获取记忆配置
     */
    public AiProperties.MemoryConfig getMemoryConfig() {
        return aiProperties.getMemory();
    }

    /**
     * 获取安全配置
     */
    public AiProperties.SecurityConfig getSecurityConfig() {
        return aiProperties.getSecurity();
    }

    /**
     * 获取并发配置
     */
    public AiProperties.ConcurrencyConfig getConcurrencyConfig() {
        return aiProperties.getConcurrency();
    }
}
