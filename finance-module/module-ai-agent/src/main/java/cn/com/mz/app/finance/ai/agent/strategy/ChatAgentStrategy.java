package cn.com.mz.app.finance.ai.agent.strategy;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.impl.ChatAgent;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通用对话策略
 * 适合日常问答和简单任务
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class ChatAgentStrategy implements AgentStrategy {

    private final ModelService modelService;
    private final ConversationService conversationService;

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return "通用对话 Agent，适合日常问答和简单任务";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return new ChatAgent(
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
            你是一个智能助手，能够帮助用户解答问题。
            请用清晰、准确的语言回答用户的问题。
            如果需要查找信息，请使用搜索工具。
            如果需要计算，请使用计算器工具。
            """;
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("search", "calculator");
    }

    @Override
    public String getIcon() {
        return "chat-icon";
    }
}
