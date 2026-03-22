package cn.com.mz.app.finance.ai.agent.impl;

import cn.com.mz.app.finance.ai.agent.BaseAgent;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 数据分析 Agent 实现
 *
 * @author mz
 */
public class DataAgent extends BaseAgent {

    private final ModelService modelService;
    private final ConversationService conversationService;

    public DataAgent(AiModuleConfig config, String systemPrompt,
                     List<String> availableTools, ModelService modelService,
                     ConversationService conversationService) {
        super("data", "数据分析", systemPrompt, availableTools, config);
        this.modelService = modelService;
        this.conversationService = conversationService;
    }

    @Override
    protected ChatResponse doChat(String conversationId, String message) {
        String convId = conversationService.getOrCreateConversation(conversationId, "data");
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
        String convId = conversationService.getOrCreateConversation(conversationId, "data");
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
