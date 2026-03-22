package cn.com.mz.app.finance.ai.agent.impl;

import cn.com.mz.app.finance.ai.agent.BaseAgent;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 通用对话 Agent 实现
 *
 * @author mz
 */
public class ChatAgent extends BaseAgent {

    private final ModelService modelService;
    private final ConversationService conversationService;

    public ChatAgent(AiModuleConfig config, String systemPrompt,
                     List<String> availableTools, ModelService modelService,
                     ConversationService conversationService) {
        super("chat", "通用助手", systemPrompt, availableTools, config);
        this.modelService = modelService;
        this.conversationService = conversationService;
    }

    @Override
    protected ChatResponse doChat(String conversationId, String message) {
        // 获取或创建会话
        String convId = conversationService.getOrCreateConversation(conversationId, "chat");

        // 添加用户消息
        conversationService.addMessage(convId, "USER", message);

        // 调用模型
        ChatResponse response = modelService.chat(
                convId,
                message,
                systemPrompt,
                availableTools,
                config,
                false
        );

        // 添加助手消息
        conversationService.addMessage(convId, "ASSISTANT", response.getContent());

        response.setConversationId(convId);
        return response;
    }

    @Override
    protected Flux<ChatResponse> doChatStream(String conversationId, String message) {
        // 获取或创建会话
        String convId = conversationService.getOrCreateConversation(conversationId, "chat");

        // 添加用户消息
        conversationService.addMessage(convId, "USER", message);

        // 调用模型流式
        return modelService.chatStream(
                        convId,
                        message,
                        systemPrompt,
                        availableTools,
                        config
                )
                .doOnNext(response -> response.setConversationId(convId))
                .doOnComplete(() -> {
                    // 流式完成后，记录最后一条消息
                    // 实际实现中需要累积内容
                });
    }
}
