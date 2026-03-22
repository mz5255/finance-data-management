package cn.com.mz.app.finance.ai.service.impl;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.model.LlmModel;
import cn.com.mz.app.finance.ai.model.ModelFactory;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 模型服务实现
 *
 * @author mz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ModelFactory modelFactory;

    @Override
    public ChatResponse chat(String conversationId, String message, String systemPrompt,
                             List<String> availableTools, AiModuleConfig config, boolean stream) {
        String modelId = config.getModelId() != null ? config.getModelId() : "zhipu";
        LlmModel model = modelFactory.getModel(modelId);

        log.debug("Chat request - conversationId: {}, model: {}", conversationId, modelId);
        return model.chat(conversationId, message, systemPrompt, availableTools, config);
    }

    @Override
    public Flux<ChatResponse> chatStream(String conversationId, String message, String systemPrompt,
                                          List<String> availableTools, AiModuleConfig config) {
        String modelId = config.getModelId() != null ? config.getModelId() : "zhipu";
        LlmModel model = modelFactory.getModel(modelId);

        log.debug("Chat stream request - conversationId: {}, model: {}", conversationId, modelId);
        return model.chatStream(conversationId, message, systemPrompt, availableTools, config);
    }

    @Override
    public float[] embed(String text, String modelId) {
        String actualModelId = modelId != null ? modelId : "zhipu";
        LlmModel model = modelFactory.getModel(actualModelId);
        return model.embed(text);
    }

    @Override
    public int countTokens(String text, String modelId) {
        String actualModelId = modelId != null ? modelId : "zhipu";
        LlmModel model = modelFactory.getModel(actualModelId);
        return model.countTokens(text);
    }
}
