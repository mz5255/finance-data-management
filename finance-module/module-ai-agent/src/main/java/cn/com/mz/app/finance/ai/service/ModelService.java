package cn.com.mz.app.finance.ai.service;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.file.AiFileProcessService;
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
     * 执行多模态对话（流式，支持图片）
     *
     * @param conversationId 会话ID
     * @param message        用户消息
     * @param images         图片列表
     * @param systemPrompt   系统提示词
     * @param availableTools 可用工具
     * @param config         配置
     * @return 响应流
     */
    Flux<ChatResponse> chatStreamWithImages(String conversationId, String message,
                                            List<AiFileProcessService.ImageData> images,
                                            String systemPrompt, List<String> availableTools,
                                            AiModuleConfig config);

    /**
     * 生成嵌入向量
     */
    float[] embed(String text, String modelId);

    /**
     * 计算Token数量
     */
    int countTokens(String text, String modelId);
}
