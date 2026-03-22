package cn.com.mz.app.finance.ai.model;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.exception.AgentException;
import cn.com.mz.app.finance.ai.model.strategy.ModelStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 模型工厂
 * 负责创建和管理 LLM 模型实例
 *
 * @author mz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelFactory {

    private final Map<String, ModelStrategy> strategies;
    private final AiProperties aiProperties;

    /**
     * 获取默认模型
     */
    public LlmModel getDefaultModel() {
        return getModel(aiProperties.getDefaultModel());
    }

    /**
     * 获取指定模型
     */
    public LlmModel getModel(String modelId) {
        ModelStrategy strategy = strategies.get(modelId + "ModelStrategy");
        if (strategy == null) {
            throw AgentException.configurationError("Unknown model: " + modelId);
        }

        AiProperties.ModelConfig config = aiProperties.getModels().get(modelId);
        if (config == null || !config.isEnabled()) {
            throw AgentException.configurationError("Model not configured or disabled: " + modelId);
        }

        return strategy.createModel(config);
    }

    /**
     * 获取所有支持的模型ID
     */
    public List<String> getSupportedModelIds() {
        return aiProperties.getModels().entrySet().stream()
                .filter(e -> e.getValue().isEnabled())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelId) {
        AiProperties.ModelConfig config = aiProperties.getModels().get(modelId);
        if (config == null || !config.isEnabled()) {
            return false;
        }

        ModelStrategy strategy = strategies.get(modelId + "ModelStrategy");
        return strategy != null;
    }
}
