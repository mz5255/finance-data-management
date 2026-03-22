package cn.com.mz.app.finance.ai.agent.impl;

import cn.com.mz.app.finance.ai.agent.BaseAgent;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

        // 用于累积 AI 回复内容
        AtomicReference<StringBuilder> contentAccumulator = new AtomicReference<>(new StringBuilder());

        // 调用模型流式
        return modelService.chatStream(
                        convId,
                        message,
                        systemPrompt,
                        availableTools,
                        config
                )
                .doOnNext(response -> {
                    response.setConversationId(convId);
                    // 累积内容
                    if (response.getContent() != null) {
                        contentAccumulator.get().append(response.getContent());
                    }
                })
                .doOnComplete(() -> {
                    // 流式完成后，保存 AI 的完整回复
                    String fullContent = contentAccumulator.get().toString();
                    if (!fullContent.isEmpty()) {
                        conversationService.addMessage(convId, "ASSISTANT", fullContent);
                    }
                })
                .doOnError(error -> {
                    // 发生错误时也保存已累积的内容
                    String partialContent = contentAccumulator.get().toString();
                    if (!partialContent.isEmpty()) {
                        conversationService.addMessage(convId, "ASSISTANT",
                                partialContent + "\n\n[错误: 响应被中断]");
                    }
                });
    }
}
