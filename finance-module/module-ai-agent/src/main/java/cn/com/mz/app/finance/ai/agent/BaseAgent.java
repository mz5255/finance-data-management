package cn.com.mz.app.finance.ai.agent;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Agent 抽象基类
 * 提供通用的 Agent 实现
 *
 * @author mz
 */
@Slf4j
@Getter
public abstract class BaseAgent implements Agent {

    protected final String type;
    protected final String name;
    protected final String systemPrompt;
    protected final List<String> availableTools;
    protected final AiModuleConfig config;

    protected BaseAgent(String type, String name, String systemPrompt,
                        List<String> availableTools, AiModuleConfig config) {
        this.type = type;
        this.name = name;
        this.systemPrompt = systemPrompt;
        this.availableTools = availableTools;
        this.config = config;
    }

    @Override
    public ChatResponse chat(String conversationId, String message) {
        log.info("Agent [{}] processing message for conversation: {}", type, conversationId);
        // 子类实现具体逻辑
        return doChat(conversationId, message);
    }

    @Override
    public Flux<ChatResponse> chatStream(String conversationId, String message) {
        log.info("Agent [{}] streaming message for conversation: {}", type, conversationId);
        // 子类实现具体逻辑
        return doChatStream(conversationId, message);
    }

    /**
     * 执行对话（非流式）- 子类实现
     */
    protected abstract ChatResponse doChat(String conversationId, String message);

    /**
     * 执行对话（流式）- 子类实现
     */
    protected abstract Flux<ChatResponse> doChatStream(String conversationId, String message);
}
