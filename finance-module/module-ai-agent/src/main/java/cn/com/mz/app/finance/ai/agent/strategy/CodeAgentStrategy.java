package cn.com.mz.app.finance.ai.agent.strategy;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.impl.CodeAgent;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 代码助手策略
 * 适合代码开发和项目分析
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class CodeAgentStrategy implements AgentStrategy {

    private final ModelService modelService;
    private final ConversationService conversationService;

    @Override
    public String getName() {
        return "code";
    }

    @Override
    public String getDescription() {
        return "代码开发和项目分析 Agent";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return new CodeAgent(
                config,
                getSystemPrompt(),
                getAvailableTools(),
                modelService,
                conversationService
        );
    }

    @Override
    public String getSystemPrompt() {
        return """
            你是一个专业的代码助手，熟悉 Java 和 Vue 开发。
            你可以：
            1. 分析现有代码
            2. 生成符合规范的代码
            3. 编写测试用例
            4. 进行代码审查
            5. 重构代码

            在修改代码时，请遵循以下规范：
            - Java 代码遵循阿里巴巴 Java 开发规范
            - Vue 代码遵循 Vue 官方风格指南
            - 所有公共方法必须有 Javadoc 注释
            - 变量命名清晰，避免使用缩写
            """;
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("project_reader", "file_reader", "file_writer", "code_search", "test_runner");
    }

    @Override
    public String getIcon() {
        return "code-icon";
    }
}
