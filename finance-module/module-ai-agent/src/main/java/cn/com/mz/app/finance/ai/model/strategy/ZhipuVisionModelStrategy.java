package cn.com.mz.app.finance.ai.model.strategy;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.model.LlmModel;
import cn.com.mz.app.finance.ai.model.impl.ZhipuModel;
import org.springframework.stereotype.Component;

/**
 * 智谱 GLM 视觉模型策略
 * 用于支持图片分析的多模态模型
 *
 * @author mz
 */
@Component("zhipu-visionModelStrategy")
public class ZhipuVisionModelStrategy implements ModelStrategy {

    @Override
    public String getName() {
        return "zhipu-vision";
    }

    @Override
    public LlmModel createModel(AiProperties.ModelConfig config) {
        return new ZhipuModel(config);
    }
}
