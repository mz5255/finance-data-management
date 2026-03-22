package cn.com.mz.app.finance.ai.model;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.exception.AgentException;
import cn.com.mz.app.finance.ai.model.strategy.ModelStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 模型路由
 * 根据模型ID路由到对应的模型实例
 *
 * @author mz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelRouter {

    private final Map<String, ModelStrategy> strategies;
    private final AiProperties aiProperties;

    /**
     * 获取模型实例
     */
    public LlmModel getModel(String modelId) {
        String actualModelId = modelId != null ? modelId : aiProperties.getDefaultModel();

        ModelStrategy strategy = strategies.get(actualModelId + "ModelStrategy");
        if (strategy == null) {
            throw AgentException.configurationError("Unknown model: " + actualModelId);
        }

        AiProperties.ModelConfig config = aiProperties.getModels().get(actualModelId);
        if (config == null) {
            config = new AiProperties.ModelConfig();
            config.setEnabled(true);
            config.setModel(actualModelId);
        }

        return strategy.createModel(config);
    }

    /**
     * 获取默认模型
     */
    public LlmModel getDefaultModel() {
        return getModel(aiProperties.getDefaultModel());
    }

    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelId) {
        AiProperties.ModelConfig config = aiProperties.getModels().get(modelId);
        if (config == null || !config.isEnabled()) {
            return false;
        }
        return strategies.containsKey(modelId + "ModelStrategy");
    }
}
