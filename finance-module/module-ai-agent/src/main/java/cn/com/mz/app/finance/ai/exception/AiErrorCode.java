package cn.com.mz.app.finance.ai.exception;

import lombok.Getter;

/**
 * AI 错误码枚举
 *
 * @author mz
 */
@Getter
public enum AiErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    RATE_LIMITED(429, "请求频率超限"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // AI 特定错误码
    MODEL_CALL_FAILED(1001, "模型调用失败"),
    TOKEN_LIMIT_EXCEEDED(1002, "Token 超限"),
    CONTEXT_OVERFLOW(1003, "上下文溢出"),
    TOOL_EXECUTION_FAILED(1004, "工具执行失败"),
    PROMPT_INJECTION_DETECTED(1005, "Prompt 注入检测"),
    SENSITIVE_DATA_DETECTED(1006, "敏感数据检测"),
    AGENT_NOT_FOUND(1007, "Agent 不存在"),
    TOOL_NOT_FOUND(1008, "工具不存在"),
    CONVERSATION_NOT_FOUND(1009, "会话不存在"),
    CONFIGURATION_ERROR(1010, "配置错误");

    private final int code;
    private final String message;

    AiErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
