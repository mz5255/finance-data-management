package cn.com.mz.app.finance.ai.agent.impl;

import cn.com.mz.app.finance.ai.agent.BaseAgent;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 代码助手 Agent 实现
 *
 * @author mz
 */
public class CodeAgent extends BaseAgent {

    private final ModelService modelService;
    private final ConversationService conversationService;

    public CodeAgent(AiModuleConfig config, String systemPrompt,
                     List<String> availableTools, ModelService modelService,
                     ConversationService conversationService) {
        super("code", "开发助手", systemPrompt, availableTools, config);
        this.modelService = modelService;
        this.conversationService = conversationService;
    }

    @Override
    protected ChatResponse doChat(String conversationId, String message) {
        String convId = conversationService.getOrCreateConversation(conversationId, "code");
        conversationService.addMessage(convId, "USER", message);

        ChatResponse response = modelService.chat(
                convId,
                message,
                systemPrompt,
                availableTools,
                config,
                false
        );

        conversationService.addMessage(convId, "ASSISTANT", response.getContent());
        response.setConversationId(convId);
        return response;
    }

    @Override
    protected Flux<ChatResponse> doChatStream(String conversationId, String message) {
        String convId = conversationService.getOrCreateConversation(conversationId, "code");
        conversationService.addMessage(convId, "USER", message);

        return modelService.chatStream(
                        convId,
                        message,
                        systemPrompt,
                        availableTools,
                        config
                )
                .doOnNext(response -> response.setConversationId(convId));
    }
}
