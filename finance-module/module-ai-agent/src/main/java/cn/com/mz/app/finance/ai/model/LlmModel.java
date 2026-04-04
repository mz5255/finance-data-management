package cn.com.mz.app.finance.ai.model;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * LLM 模型接口
 * 定义大语言模型的基本行为
 *
 * @author mz
 */
public interface LlmModel {

    /**
     * 获取模型ID
     * @return 模型ID
     */
    String getModelId();

    /**
     * 获取模型名称
     * @return 模型名称
     */
    String getModelName();

    /**
     * 执行对话（非流式）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @param systemPrompt 系统提示词
     * @param availableTools 可用工具
     * @param config 配置
     * @return 响应
     */
    ChatResponse chat(String conversationId, String message, String systemPrompt,
                      List<String> availableTools, AiModuleConfig config);

    /**
     * 执行对话（流式）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @param systemPrompt 系统提示词
     * @param availableTools 可用工具
     * @param config 配置
     * @return 响应流
     */
    Flux<ChatResponse> chatStream(String conversationId, String message, String systemPrompt,
                                   List<String> availableTools, AiModuleConfig config);

    /**
     * 执行多模态对话（流式，支持图片）
     *
     * @param conversationId 会话ID
     * @param message        用户消息
     * @param images         图片列表（base64编码）
     * @param systemPrompt   系统提示词
     * @param availableTools 可用工具
     * @param config         配置
     * @return 响应流
     */
    default Flux<ChatResponse> chatStreamWithImages(String conversationId, String message,
                                                    List<ImageContent> images, String systemPrompt,
                                                    List<String> availableTools, AiModuleConfig config) {
        // 默认实现：忽略图片，使用普通文本对话
        return chatStream(conversationId, message, systemPrompt, availableTools, config);
    }

    /**
     * 图片内容
     */
    record ImageContent(String base64Data, String mimeType) {
    }

    /**
     * 生成嵌入向量
     * @param text 文本内容
     * @return 向量数组
     */
    float[] embed(String text);

    /**
     * 计算Token数量
     * @param text 文本内容
     * @return Token数量
     */
    int countTokens(String text);

    /**
     * 检查模型是否可用
     * @return 是否可用
     */
    boolean isAvailable();
}
