package cn.com.mz.app.finance.ai.service;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 模型服务接口
 *
 * @author mz
 */
public interface ModelService {

    /**
     * 执行对话（非流式）
     */
    ChatResponse chat(String conversationId, String message, String systemPrompt,
                      List<String> availableTools, AiModuleConfig config, boolean stream);

    /**
     * 执行对话（流式）
     */
    Flux<ChatResponse> chatStream(String conversationId, String message, String systemPrompt,
                                   List<String> availableTools, AiModuleConfig config);

    /**
     * 生成嵌入向量
     */
    float[] embed(String text, String modelId);

    /**
     * 计算Token数量
     */
    int countTokens(String text, String modelId);
}
