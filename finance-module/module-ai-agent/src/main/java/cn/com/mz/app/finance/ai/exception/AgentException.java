package cn.com.mz.app.finance.ai.exception;

import lombok.Getter;

/**
 * Agent 异常
 *
 * @author mz
 */
@Getter
public class AgentException extends RuntimeException {

    private final int code;

    public AgentException(String message) {
        super(message);
        this.code = 1000;
    }

    public AgentException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AgentException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 模型调用失败
     */
    public static AgentException modelCallFailed(String message) {
        return new AgentException(1001, "模型调用失败: " + message);
    }

    /**
     * 模型调用失败（带原因）
     */
    public static AgentException modelCallFailed(String message, Throwable cause) {
        return new AgentException(1001, "模型调用失败: " + message, cause);
    }

    /**
     * Token 超限
     */
    public static AgentException tokenLimitExceeded(int current, int limit) {
        return new AgentException(1002, String.format("Token 超限: 当前 %d, 限制 %d", current, limit));
    }

    /**
     * 上下文溢出
     */
    public static AgentException contextOverflow(int currentTokens, int maxTokens) {
        return new AgentException(1003, String.format("上下文溢出: 当前 %d tokens, 最大 %d tokens", currentTokens, maxTokens));
    }

    /**
     * 工具执行失败
     */
    public static AgentException toolExecutionFailed(String toolName, String message) {
        return new AgentException(1004, String.format("工具执行失败 [%s]: %s", toolName, message));
    }

    /**
     * 工具执行失败（带原因）
     */
    public static AgentException toolExecutionFailed(String toolName, String message, Throwable cause) {
        return new AgentException(1004, String.format("工具执行失败 [%s]: %s", toolName, message), cause);
    }

    /**
     * Prompt 注入检测
     */
    public static AgentException promptInjectionDetected(String message) {
        return new AgentException(1005, "Prompt 注入检测: " + message);
    }

    /**
     * 敏感数据检测
     */
    public static AgentException sensitiveDataDetected(String dataType) {
        return new AgentException(1006, "检测到敏感数据: " + dataType);
    }

    /**
     * Agent 不存在
     */
    public static AgentException agentNotFound(String agentType) {
        return new AgentException(1007, "Agent 不存在: " + agentType);
    }

    /**
     * 工具不存在
     */
    public static AgentException toolNotFound(String toolName) {
        return new AgentException(1008, "工具不存在: " + toolName);
    }

    /**
     * 会话不存在
     */
    public static AgentException conversationNotFound(String conversationId) {
        return new AgentException(1009, "会话不存在: " + conversationId);
    }

    /**
     * 配置错误
     */
    public static AgentException configurationError(String message) {
        return new AgentException(1010, "配置错误: " + message);
    }
}
