package cn.com.mz.app.finance.ai.model.strategy;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.model.LlmModel;

/**
 * 模型策略接口
 * 定义不同模型的创建方式
 *
 * @author mz
 */
public interface ModelStrategy {

    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getName();

    /**
     * 创建模型实例
     * @param config 模型配置
     * @return 模型实例
     */
    LlmModel createModel(AiProperties.ModelConfig config);
}
