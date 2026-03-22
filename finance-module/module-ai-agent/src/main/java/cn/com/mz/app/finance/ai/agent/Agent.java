package cn.com.mz.app.finance.ai.agent;

import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Agent 接口
 * 定义智能体的基本行为
 *
 * @author mz
 */
public interface Agent {

    /**
     * 获取 Agent 类型
     * @return 类型标识
     */
    String getType();

    /**
     * 获取 Agent 名称
     * @return 名称
     */
    String getName();

    /**
     * 获取系统提示词
     * @return 系统提示词
     */
    String getSystemPrompt();

    /**
     * 获取可用工具列表
     * @return 工具名称列表
     */
    List<String> getAvailableTools();

    /**
     * 获取配置
     * @return 配置对象
     */
    AiModuleConfig getConfig();

    /**
     * 执行对话（非流式）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return 响应
     */
    ChatResponse chat(String conversationId, String message);

    /**
     * 执行对话（流式）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return 响应流
     */
    Flux<ChatResponse> chatStream(String conversationId, String message);
}
