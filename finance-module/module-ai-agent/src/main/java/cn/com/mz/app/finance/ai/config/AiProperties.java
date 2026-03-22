package cn.com.mz.app.finance.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Agent 配置属性
 * 对应 application-ai.yml 中的 ai 配置
 *
 * @author mz
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /**
     * 默认模型ID
     */
    private String defaultModel = "zhipu";

    /**
     * 默认 Agent 类型
     */
    private String defaultAgent = "chat";

    /**
     * 模型配置 Map
     * key: 模型ID (如 zhipu, claude)
     * value: 模型配置
     */
    private Map<String, ModelConfig> models = new HashMap<>();

    /**
     * Agent 配置 Map
     * key: Agent类型 (如 chat, code, data)
     * value: Agent配置
     */
    private Map<String, AgentConfig> agents = new HashMap<>();

    /**
     * 工具配置 Map
     * key: 工具名称 (如 file_reader, file_writer)
     * value: 工具配置
     */
    private Map<String, ToolConfig> tools = new HashMap<>();

    /**
     * 记忆配置
     */
    private MemoryConfig memory = new MemoryConfig();

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 并发配置
     */
    private ConcurrencyConfig concurrency = new ConcurrencyConfig();

    // ==================== 内部配置类 ====================

    /**
     * 模型配置
     */
    @Data
    public static class ModelConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * API 密钥
         */
        private String apiKey;
        /**
         * 模型名称
         */
        private String model = "glm-5";
        /**
         * API 基础地址
         */
        private String baseUrl = "https://open.bigmodel.cn/api/paas/v4";
        /**
         * 最大输出 Token 数
         */
        private int maxTokens = 4096;
        /**
         * 温度参数
         */
        private double temperature = 0.7;
        /**
         * 超时时间（毫秒）
         */
        private long timeout = 30000;
        /**
         * 重试次数
         */
        private int retryTimes = 3;
    }

    /**
     * Agent 配置
     */
    @Data
    public static class AgentConfig {
        /**
         * 使用的模型ID
         */
        private String model = "zhipu";
        /**
         * 可用工具列表
         */
        private List<String> tools = new ArrayList<>();
        /**
         * 最大上下文轮数
         */
        private int maxContextRounds = 20;
        /**
         * 系统提示词
         */
        private String systemPrompt;
    }

    /**
     * 工具配置
     */
    @Data
    public static class ToolConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * 是否需要确认
         */
        private boolean requireConfirmation = false;
        /**
         * 允许的文件扩展名
         */
        private List<String> allowedExtensions = new ArrayList<>();
        /**
         * 最大文件大小
         */
        private long maxFileSize = 1024 * 1024;
        /**
         * 允许的目录
         */
        private List<String> allowedDirectories = new ArrayList<>();
        /**
         * 项目根目录
         */
        private String projectRoot = "./";
    }

    /**
     * 记忆配置
     */
    @Data
    public static class MemoryConfig {
        /**
         * 短期记忆配置
         */
        private ShortTermConfig shortTerm = new ShortTermConfig();
        /**
         * 向量记忆配置
         */
        private VectorConfig vector = new VectorConfig();
        /**
         * 压缩配置
         */
        private CompactionConfig compaction = new CompactionConfig();
    }

    /**
     * 短期记忆配置
     */
    @Data
    public static class ShortTermConfig {
        /**
         * 最大消息数
         */
        private int maxMessages = 20;
        /**
         * 过期时间（分钟）
         */
        private int expireMinutes = 60;
    }

    /**
     * 向量记忆配置
     */
    @Data
    public static class VectorConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * 嵌入模型
         */
        private String embeddingModel = "zhipu-embedding-3";
        /**
         * 相似度阈值
         */
        private double similarityThreshold = 0.7;
        /**
         * 返回数量
         */
        private int topK = 5;
    }

    /**
     * 压缩配置
     */
    @Data
    public static class CompactionConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * 压缩策略
         */
        private String strategy = "summary";
        /**
         * 最大 Token 数
         */
        private int maxTokens = 4000;
        /**
         * 目标比例
         */
        private double targetRatio = 0.3;
    }

    /**
     * 安全配置
     */
    @Data
    public static class SecurityConfig {
        /**
         * 是否启用 Prompt 注入检测
         */
        private boolean promptInjectionCheck = true;
        /**
         * 是否启用敏感数据检测
         */
        private boolean sensitiveDataCheck = true;
        /**
         * 敏感数据模式
         */
        private List<String> sensitivePatterns = new ArrayList<>();
        /**
         * 限流配置
         */
        private RateLimitConfig rateLimit = new RateLimitConfig();
    }

    /**
     * 限流配置
     */
    @Data
    public static class RateLimitConfig {
        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * 每分钟请求数
         */
        private int requestsPerMinute = 20;
        /**
         * 每日 Token 数
         */
        private long tokensPerDay = 100000;
    }

    /**
     * 并发配置
     */
    @Data
    public static class ConcurrencyConfig {
        /**
         * 最大并发请求数
         */
        private int maxRequests = 3;
        /**
         * 队列大小
         */
        private int queueSize = 10;
        /**
         * 超时时间（秒）
         */
        private int timeoutSeconds = 300;
    }

    // ==================== 便捷方法 ====================

    /**
     * 获取指定模型的配置
     */
    public ModelConfig getModelConfig(String modelId) {
        return models.get(modelId);
    }

    /**
     * 获取指定 Agent 的配置
     */
    public AgentConfig getAgentConfig(String agentType) {
        return agents.get(agentType);
    }

    /**
     * 获取指定工具的配置
     */
    public ToolConfig getToolConfig(String toolName) {
        return tools.get(toolName);
    }

    /**
     * 检查模型是否可用
     */
    public boolean isModelEnabled(String modelId) {
        ModelConfig config = models.get(modelId);
        return config != null && config.isEnabled() && config.getApiKey() != null;
    }
}
