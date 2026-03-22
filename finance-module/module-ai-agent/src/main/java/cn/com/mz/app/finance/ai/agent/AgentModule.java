package cn.com.mz.app.finance.ai.agent;

import cn.com.mz.app.finance.ai.module.AiModule;
import cn.com.mz.app.finance.ai.module.AiModuleFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Agent 模块
 * 管理 Agent 相关的功能
 *
 * @author mz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AgentModule implements AiModule {

    private final AgentFactory agentFactory;

    @PostConstruct
    @Override
    public void initialize() {
        log.info("Agent Module initialized with types: {}", agentFactory.getSupportedTypes());
    }

    @Override
    public String getName() {
        return "agent";
    }

    @Override
    public AiModuleFactory<Agent> getFactory() {
        return agentFactory;
    }
}
