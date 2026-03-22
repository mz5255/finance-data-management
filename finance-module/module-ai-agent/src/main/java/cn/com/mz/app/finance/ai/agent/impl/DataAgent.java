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

        // 用于累积 AI 回复内容
        AtomicReference<StringBuilder> contentAccumulator = new AtomicReference<>(new StringBuilder());

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
