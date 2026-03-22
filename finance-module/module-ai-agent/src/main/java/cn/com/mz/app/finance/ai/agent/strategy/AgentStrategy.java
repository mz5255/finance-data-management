package cn.com.mz.app.finance.ai.agent.strategy;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;

import java.util.List;

/**
 * Agent 策略接口
 * 定义不同类型 Agent 的创建和行为
 *
 * @author mz
 */
public interface AgentStrategy {

    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getName();

    /**
     * 获取策略描述
     * @return 策略描述
     */
    String getDescription();

    /**
     * 创建 Agent 实例
     * @param config 配置信息
     * @return Agent 实例
     */
    Agent createAgent(AiModuleConfig config);

    /**
     * 获取系统提示词
     * @return 提示词内容
     */
    String getSystemPrompt();

    /**
     * 获取可用工具列表
     * @return 工具名称列表
     */
    List<String> getAvailableTools();

    /**
     * 获取图标
     * @return 图标名称
     */
    default String getIcon() {
        return "agent-icon";
    }
}
