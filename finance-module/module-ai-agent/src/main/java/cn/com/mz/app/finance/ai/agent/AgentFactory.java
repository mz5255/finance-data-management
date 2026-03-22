package cn.com.mz.app.finance.ai.agent;

import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.module.AiModuleFactory;
import cn.com.mz.app.finance.ai.agent.strategy.AgentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Agent 工厂
 * 负责创建不同类型的 Agent 实例
 *
 * @author mz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentFactory implements AiModuleFactory<Agent> {

    private final Map<String, AgentStrategy> strategies;

    @Override
    public Agent createDefault() {
        return createByType("chat");
    }

    @Override
    public Agent create(AiModuleConfig config) {
        String type = config.getType() != null ? config.getType() : "chat";
        AgentStrategy strategy = strategies.get(type + "AgentStrategy");
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown agent type: " + type);
        }
        return strategy.createAgent(config);
    }

    @Override
    public Agent createByType(String type) {
        AgentStrategy strategy = strategies.get(type + "AgentStrategy");
        if (strategy == null) {
            log.warn("Agent strategy not found for type: {}, using default", type);
            strategy = strategies.get("chatAgentStrategy");
        }
        if (strategy == null) {
            throw new IllegalStateException("No default agent strategy available");
        }
        return strategy.createAgent(AiModuleConfig.defaultConfig());
    }

    /**
     * 获取所有支持的 Agent 类型
     */
    public List<String> getSupportedTypes() {
        List<String> types = new ArrayList<>();
        strategies.keySet().forEach(key -> {
            if (key.endsWith("AgentStrategy")) {
                types.add(key.replace("AgentStrategy", ""));
            }
        });
        return types;
    }

    /**
     * 获取策略
     */
    public AgentStrategy getStrategy(String type) {
        return strategies.get(type + "AgentStrategy");
    }
}
