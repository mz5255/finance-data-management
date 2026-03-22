package cn.com.mz.app.finance.ai.model.strategy;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.model.LlmModel;
import cn.com.mz.app.finance.ai.model.impl.ZhipuModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 智谱 GLM 模型策略
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class ZhipuModelStrategy implements ModelStrategy {

    @Override
    public String getName() {
        return "zhipu";
    }

    @Override
    public LlmModel createModel(AiProperties.ModelConfig config) {
        return new ZhipuModel(config);
    }
}
