# PRD: AI Agent 智能体框架

## 1. 功能概述

### 1.1 背景

在财务数据管理系统中构建一个完整的 AI Agent 智能体框架，让 AI 不仅仅是「对话工具」，而是能够**自主理解、规划、执行任务**的智能助手。

### 1.2 目标

- 构建可扩展的 AI Agent 框架，支持多种智能体类型
- 实现 **Tools（工具）** 机制，让 AI 具备执行能力
- 支持**多模型接入**（智谱 GLM、Claude、GPT、本地模型等）
- 实现**记忆系统**，支持上下文和长期记忆
- 支持**多智能体协作**，处理复杂任务
- 提供统一的对话界面作为交互入口

### 1.3 核心理念

```
传统聊天机器人：用户提问 → AI 回答文本
AI Agent：用户提问 → AI 理解意图 → 规划步骤 → 调用工具执行 → 返回结果
```

---

## 2. 系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户交互层                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  Web 对话   │  │  API 接口   │  │  命令行 CLI │  │  定时任务   │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Agent 调度层                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Agent Orchestrator                              │   │
│  │  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐        │   │
│  │  │ 意图识别  │  │ 任务规划  │  │ 执行调度  │  │ 结果整合  │        │   │
│  │  └───────────┘  └───────────┘  └───────────┘  └───────────┘        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            Agent 核心层                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │ Chat Agent  │  │ Code Agent  │  │ Data Agent  │  │ Custom...   │        │
│  │  通用对话   │  │  代码助手   │  │  数据分析   │  │  自定义     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    ▼                 ▼                 ▼
┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐
│      Tools 工具层     │ │     Memory 记忆层     │ │     Model 模型层      │
│ ┌───────────────────┐ │ │ ┌───────────────────┐ │ │ ┌───────────────────┐ │
│ │ 内置工具          │ │ │ │ 短期记忆          │ │ │ │ 智谱 GLM          │ │
│ │ - 搜索            │ │ │ │ (会话上下文)      │ │ │ │ Claude            │ │
│ │ - 数据库查询      │ │ │ └───────────────────┘ │ │ │ GPT               │ │
│ │ - API调用         │ │ │ ┌───────────────────┐ │ │ │ Ollama(本地)      │ │
│ │ - 文件操作        │ │ │ │ 长期记忆          │ │ │ │ 其他...           │ │
│ │ - 代码执行        │ │ │ │ (向量存储)        │ │ │ └───────────────────┘ │
│ └───────────────────┘ │ │ └───────────────────┘ │ └───────────────────────┘ │
│ ┌───────────────────┐ │ │ ┌───────────────────┐ │                         │
│ │ 自定义工具        │ │ │ │ 向量记忆          │ │                         │
│ │ - 财务分析        │ │ │ │ (语义检索)        │ │                         │
│ │ - 报表生成        │ │ │ └───────────────────┘ │                         │
│ │ - ...             │ │ │                       │                         │
│ └───────────────────┘ │ └───────────────────────┘                         │
└───────────────────────┘                                                     │
                                                                              │
┌─────────────────────────────────────────────────────────────────────────────┐
│                            基础设施层                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  配置管理   │  │  日志监控   │  │  安全控制   │  │  持久化     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心概念

| 概念                | 说明                                |
|-------------------|-----------------------------------|
| **Agent（智能体）**    | 具备特定能力的 AI 实体，可以理解指令、规划任务、调用工具    |
| **Tool（工具）**      | Agent 可以调用的外部能力，如搜索、数据库查询、API 调用等 |
| **Memory（记忆）**    | Agent 的记忆系统，包括短期记忆和长期记忆           |
| **Model（模型）**     | 底层大语言模型，如 GLM-5、Claude、GPT 等      |
| **Prompt（提示词）**   | 定义 Agent 行为的系统提示词                 |
| **Workflow（工作流）** | 多个 Agent 协作完成复杂任务的流程              |

---

## 3. 模块化设计

### 3.1 设计模式概述

本系统采用**抽象工厂模式 + 策略模式**实现模块化架构：

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          模块化架构                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐       │
│   │ AbstractFactory │────►│   模块工厂       │────►│   策略实现       │       │
│   └─────────────────┘     └─────────────────┘     └─────────────────┘       │
│                                                                              │
│   模块列表:                                                                  │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│   │AgentModule  │  │ ToolModule  │  │MemoryModule │  │ ModelModule │        │
│   └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                          │
│   │Orchestration│  │ContextModule│  │SecurityModule│                         │
│   └─────────────┘  └─────────────┘  └─────────────┘                          │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

**设计模式应用**：

| 模式       | 应用场景   | 说明           |
|----------|--------|--------------|
| **抽象工厂** | 各模块入口  | 统一创建模块内的相关对象 |
| **策略模式** | 业务变体   | 同一功能的不同实现策略  |
| **工厂方法** | 单一对象创建 | 延迟创建到子类      |

### 3.2 模块目录结构

```
module-ai/
├── pom.xml
└── src/main/java/cn/com/mz/app/finance/ai/
    │
    ├── module/                              # 模块化核心
    │   ├── AiModule.java                    # 模块接口
    │   ├── AbstractAiModule.java            # 模块抽象基类
    │   └── ModuleRegistry.java              # 模块注册中心
    │
    ├── agent/                               # Agent 模块
    │   ├── AgentModule.java                 # 模块入口
    │   ├── AgentFactory.java                # Agent 抽象工厂
    │   ├── strategy/                        # Agent 策略
    │   │   ├── AgentStrategy.java           # 策略接口
    │   │   ├── ChatAgentStrategy.java       # 通用对话策略
    │   │   ├── CodeAgentStrategy.java       # 代码助手策略
    │   │   └── DataAgentStrategy.java       # 数据分析策略
    │   ├── Agent.java                       # Agent 接口
    │   ├── BaseAgent.java                   # Agent 基类
    │   └── state/                           # 状态机
    │       ├── AgentState.java
    │       └── AgentStateTransition.java
    │
    ├── tool/                                # Tool 模块
    │   ├── ToolModule.java                  # 模块入口
    │   ├── ToolFactory.java                 # Tool 抽象工厂
    │   ├── strategy/                        # Tool 策略
    │   │   ├── ToolStrategy.java            # 策略接口
    │   │   ├── FileToolStrategy.java        # 文件操作策略
    │   │   ├── DbToolStrategy.java          # 数据库策略
    │   │   └── ApiToolStrategy.java         # API调用策略
    │   ├── Tool.java                        # Tool 接口
    │   ├── ToolRegistry.java                # 工具注册中心
    │   └── ToolExecutor.java                # 工具执行器
    │
    ├── memory/                              # Memory 模块
    │   ├── MemoryModule.java                # 模块入口
    │   ├── MemoryFactory.java               # Memory 抽象工厂
    │   ├── strategy/                        # Memory 策略
    │   │   ├── MemoryStrategy.java          # 存储策略接口
    │   │   ├── ShortTermStrategy.java       # 短期记忆策略
    │   │   ├── LongTermStrategy.java        # 长期记忆策略
    │   │   └── VectorStrategy.java          # 向量记忆策略
    │   ├── compaction/                      # 压缩策略
    │   │   ├── CompactionStrategy.java      # 压缩策略接口
    │   │   ├── SummaryCompactionStrategy.java
    │   │   └── SlidingWindowStrategy.java
    │   ├── recovery/                        # 会话恢复
    │   │   ├── RecoveryStrategy.java        # 恢复策略接口
    │   │   └── SessionRecoveryStrategy.java
    │   └── Memory.java                      # Memory 接口
    │
    ├── model/                               # Model 模块
    │   ├── ModelModule.java                 # 模块入口
    │   ├── ModelFactory.java                # Model 抽象工厂
    │   ├── strategy/                        # Model 策略
    │   │   ├── ModelStrategy.java           # 策略接口
    │   │   ├── ZhipuStrategy.java           # 智谱策略
    │   │   ├── ClaudeStrategy.java          # Claude策略
    │   │   ├── OpenAIStrategy.java          # OpenAI策略
    │   │   └── OllamaStrategy.java          # 本地模型策略
    │   ├── LlmModel.java                    # 模型接口
    │   └── ModelRouter.java                 # 模型路由
    │
    ├── orchestration/                       # 编排模块
    │   ├── OrchestrationModule.java         # 模块入口
    │   ├── OrchestrationFactory.java        # 编排工厂
    │   ├── strategy/                        # 编排策略
    │   │   ├── TaskDecompositionStrategy.java    # 任务分解策略
    │   │   ├── ProgressTrackingStrategy.java     # 进度追踪策略
    │   │   └── RollbackStrategy.java             # 回滚策略
    │   ├── AgentOrchestrator.java           # Agent 编排器
    │   ├── ReActEngine.java                 # ReAct 引擎
    │   └── WorkflowEngine.java              # 工作流引擎
    │
    ├── context/                             # 上下文模块
    │   ├── ContextModule.java               # 模块入口
    │   ├── AgentContext.java                # Agent 上下文
    │   └── ContextManager.java              # 上下文管理器
    │
    ├── security/                            # 安全模块
    │   ├── SecurityModule.java              # 模块入口
    │   ├── strategy/                        # 安全策略
    │   │   ├── HumanLoopStrategy.java       # 人机协作策略
    │   │   ├── PermissionStrategy.java      # 权限策略
    │   │   └── AuditStrategy.java           # 审计策略
    │   └── SensitiveDataDetector.java       # 敏感数据检测
    │
    ├── config/                              # 配置
    │   ├── AiAutoConfiguration.java
    │   └── AiProperties.java
    │
    └── exception/                           # 异常
        └── AgentException.java
```

### 3.3 模块抽象工厂

#### 3.3.1 模块接口定义

```java
/**
 * AI 模块接口
 */
public interface AiModule {
    /**
     * 模块名称
     */
    String getName();

    /**
     * 模块初始化
     */
    void initialize();

    /**
     * 获取模块工厂
     */
    AiModuleFactory getFactory();
}

/**
 * AI 模块抽象工厂
 */
public interface AiModuleFactory<T> {
    /**
     * 创建默认实例
     */
    T createDefault();

    /**
     * 根据配置创建实例
     */
    T create(AiModuleConfig config);

    /**
     * 根据类型创建实例
     */
    T createByType(String type);
}
```

#### 3.3.2 模块注册中心

```java
/**
 * 模块注册中心
 */
@Service
public class ModuleRegistry {

    private final Map<String, AiModule> modules = new ConcurrentHashMap<>();

    /**
     * 注册模块
     */
    public void register(AiModule module) {
        modules.put(module.getName(), module);
    }

    /**
     * 获取模块
     */
    @SuppressWarnings("unchecked")
    public <T extends AiModule> T getModule(String name) {
        return (T) modules.get(name);
    }

    /**
     * 获取模块工厂
     */
    public <T> AiModuleFactory<T> getFactory(String moduleName) {
        AiModule module = modules.get(moduleName);
        return module != null ? module.getFactory() : null;
    }
}
```

### 3.4 Agent 模块

#### 3.4.1 Agent 抽象工厂

```java
/**
 * Agent 模块
 */
@Module
public class AgentModule implements AiModule {

    @Override
    public String getName() {
        return "agent";
    }

    @Override
    public AiModuleFactory<Agent> getFactory() {
        return new AgentFactory();
    }
}

/**
 * Agent 抽象工厂
 */
@Component
public class AgentFactory implements AiModuleFactory<Agent> {

    private final Map<String, AgentStrategy> strategies;

    @Override
    public Agent createDefault() {
        return createByType("chat");
    }

    @Override
    public Agent create(AiModuleConfig config) {
        AgentStrategy strategy = strategies.get(config.getType());
        return strategy.createAgent(config);
    }

    @Override
    public Agent createByType(String type) {
        AgentStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown agent type: " + type);
        }
        return strategy.createAgent(AiModuleConfig.defaultConfig());
    }

    /**
     * 获取所有支持的 Agent 类型
     */
    public List<String> getSupportedTypes() {
        return new ArrayList<>(strategies.keySet());
    }
}
```

#### 3.4.2 Agent 策略接口

```java
/**
 * Agent 策略接口
 */
public interface AgentStrategy {
    /**
     * 策略名称
     */
    String getName();

    /**
     * 策略描述
     */
    String getDescription();

    /**
     * 创建 Agent
     */
    Agent createAgent(AiModuleConfig config);

    /**
     * 获取系统提示词
     */
    String getSystemPrompt();

    /**
     * 获取可用工具
     */
    List<String> getAvailableTools();
}

/**
 * 通用对话策略
 */
@Component
public class ChatAgentStrategy implements AgentStrategy {

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return "通用对话 Agent，适合日常问答和简单任务";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return ChatAgent.builder()
            .systemPrompt(getSystemPrompt())
            .tools(getAvailableTools())
            .config(config)
            .build();
    }

    @Override
    public String getSystemPrompt() {
        return """
            你是一个智能助手，能够帮助用户解答问题。
            请用清晰、准确的语言回答用户的问题。
            """;
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("search", "calculator");
    }
}

/**
 * 代码助手策略
 */
@Component
public class CodeAgentStrategy implements AgentStrategy {

    @Override
    public String getName() {
        return "code";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return CodeAgent.builder()
            .systemPrompt(getSystemPrompt())
            .tools(getAvailableTools())
            .config(config)
            .build();
    }

    @Override
    public String getSystemPrompt() {
        return """
            你是一个专业的代码助手，熟悉 Java 和 Vue 开发。
            你可以：
            1. 分析现有代码
            2. 生成符合规范的代码
            3. 编写测试用例
            4. 进行代码审查
            """;
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("file_reader", "file_writer", "code_search", "test_runner");
    }
}

/**
 * 数据分析策略
 */
@Component
public class DataAgentStrategy implements AgentStrategy {

    @Override
    public String getName() {
        return "data";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return DataAgent.builder()
            .systemPrompt(getSystemPrompt())
            .tools(getAvailableTools())
            .config(config)
            .build();
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("database_query", "calculator", "chart_generator", "report_generator");
    }
}
```

#### 3.4.3 Agent 接口与基类

```java
/**
 * Agent 接口
 */
public interface Agent {
    String getId();
    String getName();
    AgentResponse execute(AgentRequest request);
    Flux<AgentResponse> executeStream(AgentRequest request);
    List<Tool> getAvailableTools();
    AgentConfig getConfig();
}

/**
 * Agent 基类
 */
@Data
public abstract class BaseAgent implements Agent {

    protected String id;
    protected String name;
    protected String systemPrompt;
    protected List<Tool> tools;
    protected AgentConfig config;
    protected AgentState state = AgentState.IDLE;

    @Override
    public AgentResponse execute(AgentRequest request) {
        validateState(AgentState.IDLE);
        setState(AgentState.RECEIVING);

        try {
            return doExecute(request);
        } finally {
            setState(AgentState.IDLE);
        }
    }

    protected abstract AgentResponse doExecute(AgentRequest request);

    protected void validateState(AgentState expected) {
        if (this.state != expected) {
            throw new AgentException("Invalid state: " + state + ", expected: " + expected);
        }
    }
}
```

### 3.5 Tool 模块

#### 3.5.1 Tool 抽象工厂

```java
/**
 * Tool 模块
 */
@Module
public class ToolModule implements AiModule {

    @Override
    public String getName() {
        return "tool";
    }

    @Override
    public AiModuleFactory<Tool> getFactory() {
        return new ToolFactory();
    }
}

/**
 * Tool 抽象工厂
 */
@Component
public class ToolFactory implements AiModuleFactory<Tool> {

    private final Map<String, ToolStrategy> strategies;

    @Override
    public Tool createDefault() {
        return createByType("file");
    }

    @Override
    public Tool createByType(String type) {
        ToolStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown tool type: " + type);
        }
        return strategy.createTool();
    }

    /**
     * 批量创建工具
     */
    public List<Tool> createTools(List<String> types) {
        return types.stream()
            .map(this::createByType)
            .collect(Collectors.toList());
    }
}
```

#### 3.5.2 Tool 策略接口

```java
/**
 * Tool 策略接口
 */
public interface ToolStrategy {
    /**
     * 策略名称
     */
    String getName();

    /**
     * 创建工具
     */
    Tool createTool();

    /**
     * 是否需要用户确认
     */
    default boolean requireConfirmation() {
        return false;
    }

    /**
     * 风险等级
     */
    default RiskLevel getRiskLevel() {
        return RiskLevel.LOW;
    }
}

/**
 * 文件工具策略
 */
@Component
public class FileToolStrategy implements ToolStrategy {

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public Tool createTool() {
        return FileTool.builder()
            .name("file_operator")
            .description("文件读写操作")
            .schema(buildSchema())
            .build();
    }

    @Override
    public boolean requireConfirmation() {
        return true;  // 文件操作需要确认
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.MEDIUM;
    }

    private String buildSchema() {
        return """
            {
                "type": "object",
                "properties": {
                    "operation": {"type": "string", "enum": ["read", "write", "delete"]},
                    "path": {"type": "string"},
                    "content": {"type": "string"}
                },
                "required": ["operation", "path"]
            }
            """;
    }
}

/**
 * 数据库工具策略
 */
@Component
public class DbToolStrategy implements ToolStrategy {

    @Override
    public String getName() {
        return "database";
    }

    @Override
    public Tool createTool() {
        return DatabaseTool.builder()
            .name("database_query")
            .description("执行 SQL 查询")
            .schema(buildSchema())
            .build();
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.HIGH;  // 数据库操作高风险
    }
}
```

### 3.6 Memory 模块

#### 3.6.1 Memory 抽象工厂

```java
/**
 * Memory 模块
 */
@Module
public class MemoryModule implements AiModule {

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public AiModuleFactory<Memory> getFactory() {
        return new MemoryFactory();
    }
}

/**
 * Memory 抽象工厂
 */
@Component
public class MemoryFactory implements AiModuleFactory<Memory> {

    private final Map<String, MemoryStrategy> strategies;
    private final Map<String, CompactionStrategy> compactionStrategies;

    @Override
    public Memory createDefault() {
        return createByType("short_term");
    }

    @Override
    public Memory createByType(String type) {
        MemoryStrategy strategy = strategies.get(type);
        return strategy.createMemory();
    }

    /**
     * 创建压缩策略
     */
    public CompactionStrategy createCompactionStrategy(String type) {
        return compactionStrategies.get(type);
    }
}
```

#### 3.6.2 Memory 策略接口

```java
/**
 * Memory 存储策略接口
 */
public interface MemoryStrategy {
    /**
     * 策略名称
     */
    String getName();

    /**
     * 创建 Memory 实例
     */
    Memory createMemory();

    /**
     * 最大容量
     */
    int getMaxCapacity();
}

/**
 * 短期记忆策略
 */
@Component
public class ShortTermStrategy implements MemoryStrategy {

    @Override
    public String getName() {
        return "short_term";
    }

    @Override
    public Memory createMemory() {
        return new ShortTermMemory(getMaxCapacity());
    }

    @Override
    public int getMaxCapacity() {
        return 10;  // 保留最近10轮
    }
}

/**
 * 长期记忆策略
 */
@Component
public class LongTermStrategy implements MemoryStrategy {

    @Override
    public String getName() {
        return "long_term";
    }

    @Override
    public Memory createMemory() {
        return new LongTermMemory();  // 持久化到数据库
    }

    @Override
    public int getMaxCapacity() {
        return Integer.MAX_VALUE;
    }
}

/**
 * 向量记忆策略
 */
@Component
public class VectorStrategy implements MemoryStrategy {

    private final VectorStoreClient vectorStore;

    @Override
    public String getName() {
        return "vector";
    }

    @Override
    public Memory createMemory() {
        return new VectorMemory(vectorStore);
    }
}
```

#### 3.6.3 压缩策略接口

```java
/**
 * 上下文压缩策略接口
 */
public interface CompactionStrategy {
    /**
     * 判断是否需要压缩
     */
    boolean shouldCompact(AgentContext context);

    /**
     * 执行压缩
     */
    CompactionResult compact(AgentContext context);
}

/**
 * 摘要压缩策略
 */
@Component
public class SummaryCompactionStrategy implements CompactionStrategy {

    private static final int MAX_TOKENS = 4000;
    private static final double TARGET_RATIO = 0.3;

    private final LlmModel model;

    @Override
    public boolean shouldCompact(AgentContext context) {
        return context.getTokenCount() > MAX_TOKENS;
    }

    @Override
    public CompactionResult compact(AgentContext context) {
        // 1. 提取重要信息
        List<Message> important = extractImportant(context);

        // 2. 生成摘要
        String summary = generateSummary(context.getMessages());

        // 3. 构建新上下文
        return CompactionResult.builder()
            .originalTokens(context.getTokenCount())
            .compressedTokens(countNewTokens(summary, important))
            .summary(summary)
            .retained(important)
            .build();
    }
}

/**
 * 滑动窗口策略
 */
@Component
public class SlidingWindowStrategy implements CompactionStrategy {

    private final int windowSize;

    @Override
    public boolean shouldCompact(AgentContext context) {
        return context.getMessageCount() > windowSize;
    }

    @Override
    public CompactionResult compact(AgentContext context) {
        // 保留最近 N 条消息
        List<Message> retained = context.getMessages()
            .subList(context.getMessageCount() - windowSize, context.getMessageCount());

        return CompactionResult.builder()
            .retained(retained)
            .build();
    }
}
```

#### 3.6.4 会话恢复策略

```java
/**
 * 会话恢复策略接口
 */
public interface RecoveryStrategy {
    /**
     * 保存会话状态
     */
    void saveSession(String conversationId, AgentContext context);

    /**
     * 恢复会话
     */
    AgentContext restoreSession(String conversationId);

    /**
     * 获取未完成的会话
     */
    List<SessionState> getIncompleteSessions(String userId);
}

/**
 * 会话恢复策略实现
 */
@Component
public class SessionRecoveryStrategy implements RecoveryStrategy {

    private final SessionStateRepository repository;

    @Override
    public void saveSession(String conversationId, AgentContext context) {
        SessionState state = SessionState.builder()
            .conversationId(conversationId)
            .userId(context.getUserId())
            .messages(context.getMessages())
            .currentPlan(context.getCurrentPlan())
            .status(SessionStatus.ACTIVE)
            .lastActiveAt(LocalDateTime.now())
            .build();

        repository.save(state);
    }

    @Override
    public AgentContext restoreSession(String conversationId) {
        SessionState state = repository.findById(conversationId).orElse(null);
        if (state == null || state.getStatus() != SessionStatus.ACTIVE) {
            return null;
        }

        return AgentContext.builder()
            .conversationId(state.getConversationId())
            .userId(state.getUserId())
            .messages(new ArrayList<>(state.getMessages()))
            .currentPlan(state.getCurrentPlan())
            .build();
    }
}
```

### 3.7 Model 模块

#### 3.7.1 Model 抽象工厂

```java
/**
 * Model 模块
 */
@Module
public class ModelModule implements AiModule {

    @Override
    public String getName() {
        return "model";
    }

    @Override
    public AiModuleFactory<LlmModel> getFactory() {
        return new ModelFactory();
    }
}

/**
 * Model 抽象工厂
 */
@Component
public class ModelFactory implements AiModuleFactory<LlmModel> {

    private final Map<String, ModelStrategy> strategies;

    @Override
    public LlmModel createDefault() {
        return createByType("zhipu");
    }

    @Override
    public LlmModel createByType(String type) {
        ModelStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown model type: " + type);
        }
        return strategy.createModel();
    }
}
```

#### 3.7.2 Model 策略接口

```java
/**
 * Model 策略接口
 */
public interface ModelStrategy {
    /**
     * 策略名称
     */
    String getName();

    /**
     * 创建模型实例
     */
    LlmModel createModel();

    /**
     * 获取默认配置
     */
    ModelConfig getDefaultConfig();

    /**
     * 是否支持流式输出
     */
    default boolean supportsStreaming() {
        return true;
    }

    /**
     * 是否支持工具调用
     */
    default boolean supportsToolCalling() {
        return true;
    }
}

/**
 * 智谱模型策略
 */
@Component
public class ZhipuStrategy implements ModelStrategy {

    @Override
    public String getName() {
        return "zhipu";
    }

    @Override
    public LlmModel createModel() {
        return ZhipuModel.builder()
            .apiKey(config.getApiKey())
            .model("glm-5")
            .maxTokens(4096)
            .build();
    }

    @Override
    public ModelConfig getDefaultConfig() {
        return ModelConfig.builder()
            .model("glm-5")
            .maxTokens(4096)
            .temperature(0.7)
            .build();
    }
}

/**
 * Claude 模型策略
 */
@Component
public class ClaudeStrategy implements ModelStrategy {

    @Override
    public String getName() {
        return "claude";
    }

    @Override
    public LlmModel createModel() {
        return ClaudeModel.builder()
            .apiKey(config.getApiKey())
            .model("claude-sonnet-4-6")
            .maxTokens(8192)
            .build();
    }

    @Override
    public ModelConfig getDefaultConfig() {
        return ModelConfig.builder()
            .model("claude-sonnet-4-6")
            .maxTokens(8192)
            .temperature(0.7)
            .build();
    }
}

/**
 * 本地模型策略 (Ollama)
 */
@Component
public class OllamaStrategy implements ModelStrategy {

    @Override
    public String getName() {
        return "ollama";
    }

    @Override
    public LlmModel createModel() {
        return OllamaModel.builder()
            .baseUrl("http://localhost:11434")
            .model("llama3")
            .build();
    }

    @Override
    public boolean supportsToolCalling() {
        return false;  // 本地模型可能不支持工具调用
    }
}
```

#### 3.7.3 模型路由策略

```java
/**
 * 模型路由策略接口
 */
public interface ModelRoutingStrategy {
    /**
     * 选择最佳模型
     */
    String selectModel(AgentRequest request);
}

/**
 * 基于规则的模型路由策略
 */
@Component
public class RuleBasedRoutingStrategy implements ModelRoutingStrategy {

    private final List<RoutingRule> rules;

    @Override
    public String selectModel(AgentRequest request) {
        // 1. 检查是否指定了模型
        if (request.getModelId() != null) {
            return request.getModelId();
        }

        // 2. 根据 Agent 类型选择
        if (request.getAgentType() == AgentType.CODE) {
            return "claude";
        }

        // 3. 根据内容模式匹配
        for (RoutingRule rule : rules) {
            if (rule.matches(request.getMessage())) {
                return rule.getModelId();
            }
        }

        // 4. 返回默认模型
        return "zhipu";
    }
}
```

### 3.8 Orchestration 模块

#### 3.8.1 Orchestration 抽象工厂

```java
/**
 * Orchestration 模块
 */
@Module
public class OrchestrationModule implements AiModule {

    @Override
    public String getName() {
        return "orchestration";
    }

    @Override
    public AiModuleFactory<Orchestrator> getFactory() {
        return new OrchestrationFactory();
    }
}

/**
 * 编排工厂
 */
@Component
public class OrchestrationFactory implements AiModuleFactory<Orchestrator> {

    private final Map<String, OrchestrationStrategy> strategies;

    @Override
    public Orchestrator createDefault() {
        return createByType("react");
    }

    @Override
    public Orchestrator createByType(String type) {
        OrchestrationStrategy strategy = strategies.get(type);
        return strategy.createOrchestrator();
    }
}
```

#### 3.8.2 任务分解策略

```java
/**
 * 任务分解策略接口
 */
public interface TaskDecompositionStrategy {
    /**
     * 分解任务
     */
    TaskPlan decompose(String userRequest, ProjectContext context);

    /**
     * 获取可执行的任务
     */
    List<SubTask> getExecutableTasks(TaskPlan plan);
}

/**
 * LLM 任务分解策略
 */
@Component
public class LlmTaskDecompositionStrategy implements TaskDecompositionStrategy {

    private final LlmModel model;

    @Override
    public TaskPlan decompose(String userRequest, ProjectContext context) {
        String prompt = """
            分析以下用户需求，将其分解为具体的子任务：

            用户需求: %s

            项目上下文:
            - 技术栈: %s
            - 现有模块: %s

            请按以下格式输出：
            1. 列出所有子任务
            2. 标注子任务之间的依赖关系
            3. 指定每个子任务需要的工具
            """.formatted(userRequest, context.getTechStack(), context.getModules());

        LlmResponse response = model.chat(LlmRequest.of(prompt));
        return parseTaskPlan(response.getContent());
    }

    @Override
    public List<SubTask> getExecutableTasks(TaskPlan plan) {
        return plan.getSubTasks().stream()
            .filter(task -> task.getStatus() == TaskStatus.PENDING)
            .filter(task -> allDependenciesMet(plan, task))
            .collect(Collectors.toList());
    }
}
```

#### 3.8.3 进度追踪策略

```java
/**
 * 进度追踪策略接口
 */
public interface ProgressTrackingStrategy {
    /**
     * 更新进度
     */
    void updateProgress(String planId, Progress progress);

    /**
     * 订阅进度更新
     */
    Flux<Progress> subscribe(String planId);
}

/**
 * WebSocket 进度追踪策略
 */
@Component
public class WebSocketProgressStrategy implements ProgressTrackingStrategy {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, FluxSink<Progress>> sinks = new ConcurrentHashMap<>();

    @Override
    public void updateProgress(String planId, Progress progress) {
        // 推送给前端
        messagingTemplate.convertAndSend("/topic/progress/" + planId, progress);

        // 通知订阅者
        FluxSink<Progress> sink = sinks.get(planId);
        if (sink != null) {
            sink.next(progress);
        }
    }

    @Override
    public Flux<Progress> subscribe(String planId) {
        return Flux.create(sink -> {
            sinks.put(planId, sink);
            sink.onDispose(() -> sinks.remove(planId));
        });
    }
}
```

#### 3.8.4 回滚策略

```java
/**
 * 回滚策略接口
 */
public interface RollbackStrategy {
    /**
     * 记录变更
     */
    void recordChange(FileChangeRecord record);

    /**
     * 回滚单个变更
     */
    void rollbackSingle(String changeId);

    /**
     * 回滚整个计划
     */
    void rollbackPlan(String planId);

    /**
     * 预览回滚影响
     */
    RollbackPreview previewRollback(String planId);
}

/**
 * 文件回滚策略
 */
@Component
public class FileRollbackStrategy implements RollbackStrategy {

    private final FileChangeRepository repository;
    private final FileService fileService;

    @Override
    public void recordChange(FileChangeRecord record) {
        repository.save(record);
    }

    @Override
    public void rollbackSingle(String changeId) {
        FileChangeRecord record = repository.findById(changeId)
            .orElseThrow(() -> new NotFoundException("变更记录不存在"));

        switch (record.getType()) {
            case CREATE -> fileService.delete(record.getFilePath());
            case MODIFY, DELETE -> fileService.write(record.getFilePath(), record.getOriginalContent());
        }

        repository.delete(record);
    }

    @Override
    public void rollbackPlan(String planId) {
        List<FileChangeRecord> records = repository.findByPlanIdOrderByTimestampDesc(planId);
        for (FileChangeRecord record : records) {
            rollbackSingle(record.getChangeId());
        }
    }

    @Override
    public RollbackPreview previewRollback(String planId) {
        List<FileChangeRecord> records = repository.findByPlanId(planId);

        return RollbackPreview.builder()
            .totalChanges(records.size())
            .createdFiles(countByType(records, ChangeType.CREATE))
            .modifiedFiles(countByType(records, ChangeType.MODIFY))
            .deletedFiles(countByType(records, ChangeType.DELETE))
            .build();
    }
}
```

### 3.9 Security 模块

#### 3.9.1 Security 抽象工厂

```java
/**
 * Security 模块
 */
@Module
public class SecurityModule implements AiModule {

    @Override
    public String getName() {
        return "security";
    }

    @Override
    public AiModuleFactory<SecurityService> getFactory() {
        return new SecurityFactory();
    }
}
```

#### 3.9.2 人机协作策略

```java
/**
 * 人机协作策略接口
 */
public interface HumanLoopStrategy {
    /**
     * 请求用户确认
     */
    CompletableFuture<Boolean> requestConfirmation(ConfirmationRequest request);

    /**
     * 处理用户响应
     */
    void handleResponse(String requestId, boolean approved, String comment);

    /**
     * 判断是否需要确认
     */
    boolean requiresConfirmation(ToolCall call, Tool tool);
}

/**
 * WebSocket 人机协作策略
 */
@Component
public class WebSocketHumanLoopStrategy implements HumanLoopStrategy {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, CompletableFuture<Boolean>> pendingConfirmations = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Boolean> requestConfirmation(ConfirmationRequest request) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingConfirmations.put(request.getRequestId(), future);

        // 推送给前端
        messagingTemplate.convertAndSendToUser(
            request.getUserId(),
            "/queue/confirmation",
            request
        );

        // 设置超时
        future.orTimeout(5, TimeUnit.MINUTES, () -> {
            pendingConfirmations.remove(request.getRequestId());
        });

        return future;
    }

    @Override
    public void handleResponse(String requestId, boolean approved, String comment) {
        CompletableFuture<Boolean> future = pendingConfirmations.remove(requestId);
        if (future != null) {
            future.complete(approved);
        }
    }

    @Override
    public boolean requiresConfirmation(ToolCall call, Tool tool) {
        // 危险工具需要确认
        if (tool.requireConfirmation()) {
            return true;
        }

        // 涉及敏感数据需要确认
        if (containsSensitiveData(call)) {
            return true;
        }

        return false;
    }
}
```

#### 3.9.3 权限策略

```java
/**
 * 权限策略接口
 */
public interface PermissionStrategy {
    /**
     * 检查工具权限
     */
    boolean hasToolPermission(String userId, String toolName);

    /**
     * 检查文件访问权限
     */
    boolean hasFilePermission(String userId, String filePath, FilePermission permission);
}

/**
 * RBAC 权限策略
 */
@Component
public class RbacPermissionStrategy implements PermissionStrategy {

    private final PermissionService permissionService;

    @Override
    public boolean hasToolPermission(String userId, String toolName) {
        return permissionService.hasPermission(userId, "tool:" + toolName);
    }

    @Override
    public boolean hasFilePermission(String userId, String filePath, FilePermission permission) {
        // 检查路径权限
        String resource = "file:" + filePath;
        String action = permission.name().toLowerCase();
        return permissionService.hasPermission(userId, resource, action);
    }
}
```

#### 3.9.4 审计策略

```java
/**
 * 审计策略接口
 */
public interface AuditStrategy {
    /**
     * 记录操作日志
     */
    void log(AuditEvent event);

    /**
     * 查询审计日志
     */
    Page<AuditLog> query(AuditQuery query);

    /**
     * 检查异常行为
     */
    List<SecurityAlert> checkAnomalies(String userId);
}

/**
 * 数据库审计策略
 */
@Component
public class DatabaseAuditStrategy implements AuditStrategy {

    private final AuditLogRepository repository;

    @Override
    public void log(AuditEvent event) {
        AuditLog log = AuditLog.builder()
            .userId(event.getUserId())
            .operation(event.getOperation())
            .resource(event.getResource())
            .result(event.getResult())
            .ipAddress(event.getIpAddress())
            .timestamp(LocalDateTime.now())
            .build();

        repository.save(log);
    }

    @Override
    public List<SecurityAlert> checkAnomalies(String userId) {
        List<SecurityAlert> alerts = new ArrayList<>();

        // 1小时内操作超过100次
        long recentCount = repository.countByUserIdAndTimestampAfter(
            userId, LocalDateTime.now().minusHours(1)
        );
        if (recentCount > 100) {
            alerts.add(new SecurityAlert("HIGH_FREQUENCY", "操作频率异常"));
        }

        return alerts;
    }
}
```

### 3.10 模块协作流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          模块协作流程                                        │
└─────────────────────────────────────────────────────────────────────────────┘

用户请求
    │
    ▼
┌─────────────────┐
│ ModuleRegistry  │ ← 获取各模块工厂
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          AgentFactory                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 根据 AgentType 选择策略 → ChatAgentStrategy / CodeAgentStrategy    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          ModelFactory                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 根据配置选择模型 → ZhipuStrategy / ClaudeStrategy / OllamaStrategy  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          ToolFactory                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 根据 Agent 需要创建工具 → FileToolStrategy / DbToolStrategy         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          MemoryFactory                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 创建记忆系统 → ShortTermStrategy + VectorStrategy                    │   │
│  │ 创建压缩策略 → SummaryCompactionStrategy                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          OrchestrationFactory                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 任务分解 → LlmTaskDecompositionStrategy                              │   │
│  │ 进度追踪 → WebSocketProgressStrategy                                 │   │
│  │ 回滚机制 → FileRollbackStrategy                                      │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          SecurityFactory                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 人机协作 → WebSocketHumanLoopStrategy                                │   │
│  │ 权限控制 → RbacPermissionStrategy                                    │   │
│  │ 安全审计 → DatabaseAuditStrategy                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
    执行并返回结果
    }
}
```

#### 3.4.3 状态流程图

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Agent 状态流转图                               │
└──────────────────────────────────────────────────────────────────────┘

    ┌──────┐
    │ IDLE │ ◄─────────────────────────────────────────────┐
    └──┬───┘                                               │
       │ 接收请求                                           │
       ▼                                                   │
  ┌──────────┐                                             │
  │RECEIVING │                                             │
  └────┬─────┘                                             │
       │                                                   │
       ▼                                                   │
  ┌──────────┐     ┌─────────┐                            │
  │ THINKING │────►│PLANNING │                            │
  └────┬─────┘     └────┬────┘                            │
       │                │                                  │
       │                ▼                                  │
       │         ┌─────────────┐                          │
       │         │TOOL_CALLING │                          │
       │         └──────┬──────┘                          │
       │                │                                  │
       │                ▼                                  │
       │         ┌─────────────┐                          │
       │         │TOOL_WAITING │                          │
       │         └──────┬──────┘                          │
       │                │                                  │
       │    ┌───────────┴───────────┐                     │
       │    │                       │                     │
       │    ▼                       ▼                     │
       │ ┌─────────────┐     ┌─────────────┐              │
       │ │WAITING_CONFIRM│   │  THINKING   │──────────────┤
       │ └──────┬──────┘     └─────────────┘              │
       │        │               (继续思考)                  │
       │        │ 用户确认                                   │
       │        ▼                                           │
       │  ┌──────────┐                                     │
       └─►│RESPONDING│                                     │
          └────┬─────┘                                     │
               │                                           │
               ▼                                           │
         ┌──────────┐                                      │
         │COMPLETED │──────────────────────────────────────┘
         └──────────┘
```

---

## 4. 工具系统设计

### 4.1 工具注册机制

```java
@Component
public class ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    /**
     * 注册工具
     */
    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
    }

    /**
     * 批量注册
     */
    public void registerAll(List<Tool> toolList) {
        toolList.forEach(this::register);
    }

    /**
     * 获取工具
     */
    public Tool get(String name) {
        return tools.get(name);
    }

    /**
     * 获取所有工具描述（供 AI 识别）
     */
    public List<ToolDescriptor> getAllDescriptors() {
        return tools.values().stream()
            .map(this::toDescriptor)
            .collect(Collectors.toList());
    }
}
```

### 4.2 内置工具列表

#### 4.2.1 项目读取工具 (ProjectReaderTool)

```json
{
  "name": "project_reader",
  "description": "读取项目结构，了解项目的模块、文件组织、技术栈等",
  "parameters": {
    "type": "object",
    "properties": {
      "action": {
        "type": "string",
        "enum": ["structure", "tech_stack", "module_info"],
        "description": "操作类型：structure=目录结构，tech_stack=技术栈，module_info=模块详情"
      },
      "module": {
        "type": "string",
        "description": "模块名（可选，用于 module_info）"
      }
    },
    "required": ["action"]
  }
}
```

#### 4.2.2 文件读取工具 (FileReaderTool)

```json
{
  "name": "file_reader",
  "description": "读取指定文件的内容",
  "parameters": {
    "type": "object",
    "properties": {
      "path": {
        "type": "string",
        "description": "文件路径（相对于项目根目录）"
      },
      "startLine": {
        "type": "integer",
        "description": "起始行号（可选）"
      },
      "endLine": {
        "type": "integer",
        "description": "结束行号（可选）"
      }
    },
    "required": ["path"]
  }
}
```

#### 4.2.3 文件写入工具 (FileWriterTool)

```json
{
  "name": "file_writer",
  "description": "创建或修改文件，用于生成 PRD、技术方案、代码、测试等",
  "parameters": {
    "type": "object",
    "properties": {
      "path": {
        "type": "string",
        "description": "文件路径（相对于项目根目录）"
      },
      "content": {
        "type": "string",
        "description": "文件内容"
      },
      "action": {
        "type": "string",
        "enum": ["create", "overwrite", "append"],
        "description": "操作类型：create=创建新文件，overwrite=覆盖，append=追加"
      }
    },
    "required": ["path", "content", "action"]
  },
  "requireConfirmation": true
}
```

#### 4.2.4 代码搜索工具 (CodeSearchTool)

```json
{
  "name": "code_search",
  "description": "搜索代码，按类名、方法名、关键词查找",
  "parameters": {
    "type": "object",
    "properties": {
      "type": {
        "type": "string",
        "enum": ["class", "method", "keyword", "annotation"],
        "description": "搜索类型：class=类名，method=方法名，keyword=关键词，annotation=注解"
      },
      "query": {
        "type": "string",
        "description": "搜索内容"
      },
      "filePattern": {
        "type": "string",
        "description": "文件匹配模式（可选），如 *.java, *.vue"
      }
    },
    "required": ["type", "query"]
  }
}
```

### 4.3 工具执行流程

```
用户请求 → Agent 分析意图 → 决定调用工具 → 生成工具调用参数
    ↓
安全检查（权限、参数校验）→ 是否需要确认？
    ↓                           ↓
    是                       否
    ↓                        ↓
用户确认                   直接执行
    ↓                        ↓
    └──────── 执行工具 ←───────┘
                ↓
           返回结果 → Agent 整合 → 返回用户
```

### 4.4 自定义工具开发指南

```java
@Component
public class FinanceReportTool implements Tool {

    @Override
    public String getName() {
        return "finance_report";
    }

    @Override
    public String getDescription() {
        return "生成财务分析报表";
    }

    @Override
    public String getParametersSchema() {
        return """
        {
            "type": "object",
            "properties": {
                "reportType": {
                    "type": "string",
                    "enum": ["income", "expense", "balance"],
                    "description": "报表类型"
                },
                "period": {
                    "type": "string",
                    "description": "时间周期，如: 2026-03"
                }
            },
            "required": ["reportType", "period"]
        }
        """;
    }

    @Override
    public ToolResult execute(Map<String, Object> parameters) {
        String reportType = (String) parameters.get("reportType");
        String period = (String) parameters.get("period");

        // 业务逻辑
        Report report = reportService.generate(reportType, period);

        return ToolResult.success(report);
    }

    @Override
    public boolean requireConfirmation() {
        return false; // 只读操作，无需确认
    }
}
```

### 4.5 回滚机制

#### 4.5.1 文件变更记录

```java
/**
 * 文件变更类型
 */
public enum ChangeType {
    CREATE,      // 创建新文件
    MODIFY,      // 修改文件
    DELETE       // 删除文件
}

/**
 * 文件变更记录
 */
@Data
@Builder
public class FileChangeRecord {
    private String changeId;               // 变更ID
    private String planId;                 // 所属计划ID
    private String filePath;               // 文件路径
    private ChangeType type;               // 变更类型
    private String originalContent;        // 原始内容（修改前）
    private String newContent;             // 新内容
    private String checksum;               // 内容校验和
    private LocalDateTime timestamp;       // 变更时间
}
```

#### 4.5.2 回滚服务

```java
/**
 * 回滚服务
 */
@Service
public class RollbackService {

    private final FileChangeRepository repository;
    private final FileService fileService;

    /**
     * 记录文件变更
     */
    public void recordChange(String planId, String filePath, ChangeType type,
                             String original, String newContent) {
        FileChangeRecord record = FileChangeRecord.builder()
            .changeId(UUID.randomUUID().toString())
            .planId(planId)
            .filePath(filePath)
            .type(type)
            .originalContent(original)
            .newContent(newContent)
            .checksum(DigestUtils.md5Hex(newContent))
            .timestamp(LocalDateTime.now())
            .build();

        repository.save(record);
    }

    /**
     * 回滚单个文件
     */
    public void rollbackFile(String changeId) {
        FileChangeRecord record = repository.findById(changeId)
            .orElseThrow(() -> new NotFoundException("变更记录不存在"));

        switch (record.getType()) {
            case CREATE -> {
                // 删除新创建的文件
                fileService.delete(record.getFilePath());
            }
            case MODIFY -> {
                // 恢复原始内容
                fileService.write(record.getFilePath(), record.getOriginalContent());
            }
            case DELETE -> {
                // 恢复被删除的文件
                fileService.write(record.getFilePath(), record.getOriginalContent());
            }
        }

        // 删除变更记录
        repository.delete(record);
    }

    /**
     * 回滚整个计划的所有变更
     */
    public void rollbackPlan(String planId) {
        List<FileChangeRecord> records = repository
            .findByPlanIdOrderByTimestampDesc(planId);

        for (FileChangeRecord record : records) {
            rollbackFile(record.getChangeId());
        }
    }

    /**
     * 预览回滚影响
     */
    public RollbackPreview previewRollback(String planId) {
        List<FileChangeRecord> records = repository.findByPlanId(planId);

        return RollbackPreview.builder()
            .totalChanges(records.size())
            .createdFiles(records.stream().filter(r -> r.getType() == ChangeType.CREATE).count())
            .modifiedFiles(records.stream().filter(r -> r.getType() == ChangeType.MODIFY).count())
            .deletedFiles(records.stream().filter(r -> r.getType() == ChangeType.DELETE).count())
            .build();
    }
}
```

#### 4.5.3 前端交互设计

```
┌─────────────────────────────────────────────────────────────┐
│  ⚠️ 代码已生成，是否继续？                                   │
│                                                              │
│  变更文件：                                                  │
│  • UserController.java (新增)                               │
│  • UserService.java (修改)                                  │
│  • UserManage.vue (新增)                                    │
│                                                              │
│  [✓ 确认保留]  [↩ 回滚全部]  [选择回滚]                      │
└─────────────────────────────────────────────────────────────┘
```

#### 4.5.4 数据库表设计

```sql
CREATE TABLE ai_file_change (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    change_id VARCHAR(64) NOT NULL COMMENT '变更ID',
    plan_id VARCHAR(64) NOT NULL COMMENT '所属计划ID',
    file_path VARCHAR(512) NOT NULL COMMENT '文件路径',
    change_type VARCHAR(32) NOT NULL COMMENT '变更类型:CREATE/MODIFY/DELETE',
    original_content LONGTEXT COMMENT '原始内容',
    new_content LONGTEXT COMMENT '新内容',
    checksum VARCHAR(64) COMMENT '内容校验和',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_plan_id (plan_id),
    INDEX idx_change_id (change_id)
) COMMENT 'AI文件变更记录表';
```

---

## 5. 记忆系统设计

### 5.1 三层记忆架构

```
┌─────────────────────────────────────────────────────────────┐
│                       Memory System                         │
├─────────────────┬─────────────────┬─────────────────────────┤
│   Short-term    │    Long-term    │      Vector Memory      │
│     Memory      │     Memory      │     (Semantic Search)   │
├─────────────────┼─────────────────┼─────────────────────────┤
│  会话级上下文    │  持久化存储      │  向量嵌入 + 相似检索     │
│  最近 N 轮对话  │  用户偏好        │  知识库检索             │
│  临时变量       │  历史记录        │  上下文增强             │
├─────────────────┼─────────────────┼─────────────────────────┤
│  Redis / 内存   │   MySQL         │   Milvus / pgvector     │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### 5.2 记忆数据模型

```java
@Data
public class MemoryItem {
    private String id;                    // 记忆ID
    private String conversationId;        // 会话ID
    private String userId;                // 用户ID
    private MemoryType type;              // 类型：USER/ASSISTANT/SYSTEM/TOOL
    private String content;               // 内容
    private Map<String, Object> metadata; // 元数据
    private float[] embedding;            // 向量嵌入（可选）
    private LocalDateTime createdAt;      // 创建时间
    private LocalDateTime expiresAt;      // 过期时间
}
```

### 5.3 向量记忆详细设计

#### 5.3.1 为什么需要向量记忆？

```
场景1：用户问"怎么添加新菜单？"
      AI 能找到之前讨论过的"菜单管理功能"相关对话

场景2：用户问"用户表在哪？"
      AI 能找到项目中的 User 实体类、UserService 等

场景3：用户说"继续上次的功能"
      AI 能回忆起上次的对话内容和进度
```

#### 5.3.2 技术选型

| 组件                | 方案               | 说明               |
|-------------------|------------------|------------------|
| **向量数据库**         | MySQL + JSON（初期） | 简单，无需额外组件        |
| **Embedding API** | 智谱 Embedding API | 和 GLM-5 配套，中文效果好 |
| **相似度计算**         | 余弦相似度            | 标准做法             |

**为什么初期用 MySQL？**

- 不需要部署额外的向量数据库（Milvus、pgvector）
- 数据量小时性能足够
- 后期数据量大了再迁移到专业向量数据库

#### 5.3.3 数据库表设计

```sql
-- 向量记忆表
CREATE TABLE ai_memory_vector (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) COMMENT '会话ID（可选）',
    memory_type VARCHAR(32) NOT NULL COMMENT '记忆类型：CHAT/DOCUMENT/CODE',
    title VARCHAR(255) COMMENT '标题（用于展示）',
    content TEXT NOT NULL COMMENT '原始内容',
    content_type VARCHAR(32) COMMENT '内容类型：USER/ASSISTANT/DOCUMENT/CODE',
    embedding JSON COMMENT '向量嵌入（float数组）',
    embedding_model VARCHAR(64) COMMENT 'embedding模型',
    metadata JSON COMMENT '元数据（文件路径、类名等）',
    importance INT DEFAULT 5 COMMENT '重要性 1-10',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user_id (user_id),
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_memory_type (memory_type),
    INDEX idx_created_at (created_at)
) COMMENT 'AI向量记忆表';
```

#### 5.3.4 记忆类型说明

| 类型           | 存什么  | 例子              |
|--------------|------|-----------------|
| **CHAT**     | 对话记录 | 用户问了什么，AI 回答了什么 |
| **DOCUMENT** | 项目文档 | PRD、技术方案、接口文档   |
| **CODE**     | 代码片段 | 关键类、方法、配置文件     |

#### 5.3.5 核心接口设计

```java
public interface VectorMemory {

    /**
     * 存储记忆
     * @param content 原始内容
     * @param type 记忆类型
     * @param metadata 元数据
     * @return 记忆ID
     */
    String store(String userId, String content, MemoryType type, Map<String, Object> metadata);

    /**
     * 相似度检索
     * @param query 查询内容
     * @param topK 返回前K个最相关的
     * @param type 过滤类型（可选）
     * @return 相关记忆列表
     */
    List<MemoryItem> search(String userId, String query, int topK, MemoryType type);

    /**
     * 根据会话ID获取记忆
     */
    List<MemoryItem> getByConversationId(String conversationId);

    /**
     * 删除记忆
     */
    void delete(String memoryId);
}
```

#### 5.3.6 向量检索流程

```
┌─────────────────────────────────────────────────────────────────┐
│                     向量检索流程                                 │
└─────────────────────────────────────────────────────────────────┘

用户提问: "怎么添加新菜单？"
    │
    ▼
┌─────────────────────┐
│ 1. 转换为向量        │  调用智谱 Embedding API
│    "怎么添加新菜单"  │  → [0.12, 0.85, 0.33, ...]
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ 2. 数据库检索        │  SELECT * FROM ai_memory_vector
│    计算相似度        │  WHERE user_id = 'xxx'
│                      │  ORDER BY cosine_similarity(embedding, ?)
│                      │  LIMIT 5
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. 返回相关记忆                                                  │
│                                                                  │
│   [相似度: 0.92] CHAT - 用户：帮我做一个菜单管理功能              │
│   [相似度: 0.88] DOCUMENT - PRD: 菜单管理模块设计                │
│   [相似度: 0.85] CODE - MenuController.java: 新增菜单接口        │
│   [相似度: 0.82] CHAT - AI：菜单需要支持树形结构...              │
│   [相似度: 0.78] CODE - MenuService.java: 保存菜单方法           │
└──────────┬──────────────────────────────────────────────────────┘
           │
           ▼
┌─────────────────────┐
│ 4. 构建上下文        │  将相关记忆作为上下文
│    发送给 LLM        │  让 AI 知道之前讨论过的内容
└─────────────────────┘
```

#### 5.3.7 余弦相似度计算（MySQL）

```sql
-- MySQL 中计算余弦相似度（存储过程或Java代码中计算）
-- 公式：cos(A,B) = (A·B) / (|A| * |B|)

-- 方案1：Java 中计算（推荐）
-- 从数据库取出所有向量，在 Java 中计算相似度

-- 方案2：MySQL 8.0+ JSON 函数（性能较差，数据量小时可用）
SELECT id, content,
       (
           SELECT SUM(json_extract(embedding, CONCAT('$[', idx, ']')) *
                      json_extract(@query_vec, CONCAT('$[', idx, ']')))
           FROM json_table(
               JSON_KEYS(embedding),
               '$[*]' COLUMNS(idx INT PATH '$')
           ) AS indices
       ) / (
           SQRT((SELECT SUM(POW(v, 2)) FROM JSON_TABLE(embedding, '$[*]' COLUMNS(v FLOAT PATH '$')) AS t1)) *
           SQRT((SELECT SUM(POW(v, 2)) FROM JSON_TABLE(@query_vec, '$[*]' COLUMNS(v FLOAT PATH '$')) AS t2))
       ) AS similarity
FROM ai_memory_vector
WHERE user_id = ?
ORDER BY similarity DESC
LIMIT 5;
```

#### 5.3.8 Java 实现示例

```java
@Service
public class VectorMemoryImpl implements VectorMemory {

    @Resource
    private ZhipuModel zhipuModel;  // 智谱模型

    @Resource
    private AiMemoryVectorMapper memoryMapper;

    /**
     * 存储记忆
     */
    @Override
    public String store(String userId, String content, MemoryType type, Map<String, Object> metadata) {
        // 1. 调用 Embedding API 获取向量
        float[] embedding = zhipuModel.embed(content);

        // 2. 保存到数据库
        AiMemoryVector memory = new AiMemoryVector();
        memory.setUserId(userId);
        memory.setContent(content);
        memory.setMemoryType(type.name());
        memory.setEmbedding(JsonUtils.toJson(embedding));
        memory.setEmbeddingModel("zhipu-embedding");
        memory.setMetadata(metadata != null ? JsonUtils.toJson(metadata) : null);
        memory.setCreatedAt(LocalDateTime.now());

        memoryMapper.insert(memory);
        return memory.getId();
    }

    /**
     * 相似度检索
     */
    @Override
    public List<MemoryItem> search(String userId, String query, int topK, MemoryType type) {
        // 1. 查询向量
        float[] queryEmbedding = zhipuModel.embed(query);

        // 2. 从数据库获取该用户的所有记忆
        List<AiMemoryVector> memories = memoryMapper.selectByUserId(userId, type);

        // 3. 计算相似度并排序
        return memories.stream()
            .map(m -> {
                float[] storedEmbedding = JsonUtils.parseArray(m.getEmbedding(), float[].class);
                double similarity = cosineSimilarity(queryEmbedding, storedEmbedding);
                return new MemoryItem(m, similarity);
            })
            .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
            .limit(topK)
            .collect(Collectors.toList());
    }

    /**
     * 计算余弦相似度
     */
    private double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

#### 5.3.9 智谱 Embedding API

```java
/**
 * 调用智谱 Embedding API
 */
public float[] embed(String text) {
    String url = "https://open.bigmodel.cn/api/paas/v4/embeddings";

    Map<String, Object> request = new HashMap<>();
    request.put("model", "embedding-3");  // 智谱的 embedding 模型
    request.put("input", text);

    String response = httpClient.post(url, request, headers);
    JSONObject json = JSON.parseObject(response);

    // 解析返回的向量
    JSONArray embeddingArray = json.getJSONArray("data")
        .getJSONObject(0)
        .getJSONArray("embedding");

    float[] embedding = new float[embeddingArray.size()];
    for (int i = 0; i < embeddingArray.size(); i++) {
        embedding[i] = embeddingArray.getFloatValue(i);
    }

    return embedding;  // 返回 1024 维向量
}
```

#### 5.3.10 记忆存储时机

| 时机      | 存储内容     | 类型       |
|---------|----------|----------|
| 用户发送消息  | 用户的问题    | CHAT     |
| AI 回复完成 | AI 的回复   | CHAT     |
| 生成 PRD  | PRD 文档内容 | DOCUMENT |
| 生成技术方案  | 技术方案内容   | DOCUMENT |
| 分析代码    | 关键代码片段   | CODE     |

#### 5.3.11 记忆清理策略

```java
/**
 * 记忆清理策略
 */
@Component
public class MemoryCleanupTask {

    @Scheduled(cron = "0 0 3 * * ?")  // 每天凌晨3点执行
    public void cleanup() {
        // 1. 删除超过90天的低重要性记忆
        memoryMapper.deleteOldMemories(90, 3);  // 重要性<=3的

        // 2. 压缩相似的记忆（可选）
        // compressSimilarMemories();
    }
}
```

---

### 5.4 记忆管理策略

| 策略        | 说明                  |
|-----------|---------------------|
| **滑动窗口**  | 保留最近 N 轮对话（默认 10 轮） |
| **摘要压缩**  | 超过窗口大小时，生成摘要替代旧对话   |
| **重要性评分** | 根据内容重要性决定保留优先级      |
| **向量检索**  | 根据当前问题检索相关历史记忆      |

### 5.5 上下文压缩策略

#### 5.5.1 压缩策略接口

```java
/**
 * 上下文压缩策略
 */
public interface ContextCompactionStrategy {
    /**
     * 判断是否需要压缩
     */
    boolean shouldCompact(AgentContext context);

    /**
     * 执行压缩
     */
    CompactionResult compact(AgentContext context);
}

/**
 * 压缩结果
 */
@Data
@Builder
public class CompactionResult {
    private int originalTokens;      // 原始 Token 数
    private int compressedTokens;    // 压缩后 Token 数
    private String summary;          // 生成的摘要
    private List<Message> retained;  // 保留的消息
}
```

#### 5.5.2 摘要压缩策略实现

```java
/**
 * 摘要压缩策略
 */
@Service
public class SummaryCompactionStrategy implements ContextCompactionStrategy {

    private static final int MAX_TOKENS = 4000;
    private static final double TARGET_RATIO = 0.3;  // 压缩到 30%
    private static final int RETAIN_RECENT = 5;       // 保留最近 5 轮

    private final LlmModel model;

    @Override
    public boolean shouldCompact(AgentContext context) {
        return context.getTokenCount() > MAX_TOKENS;
    }

    @Override
    public CompactionResult compact(AgentContext context) {
        int originalTokens = context.getTokenCount();

        // 1. 识别重要信息（决策、结论、关键代码）
        List<Message> importantMessages = extractImportantMessages(context);

        // 2. 分离需要压缩的消息
        List<Message> toCompress = getMessagesToCompress(context);

        // 3. 用 LLM 生成摘要
        String summary = generateSummary(toCompress);

        // 4. 构建新上下文：最近5轮 + 摘要 + 重要信息
        List<Message> retained = new ArrayList<>();
        retained.add(Message.system("历史摘要:\n" + summary));
        retained.addAll(importantMessages);
        retained.addAll(getRecentMessages(context, RETAIN_RECENT));

        return CompactionResult.builder()
            .originalTokens(originalTokens)
            .compressedTokens(countTokens(retained))
            .summary(summary)
            .retained(retained)
            .build();
    }

    private String generateSummary(List<Message> messages) {
        String prompt = """
            请将以下对话历史压缩成一个简洁的摘要，保留：
            1. 关键决策和结论
            2. 重要的代码变更
            3. 未解决的问题

            对话历史：
            %s
            """.formatted(formatMessages(messages));

        return model.chat(LlmRequest.of(prompt)).getContent();
    }

    private List<Message> extractImportantMessages(AgentContext context) {
        return context.getMessages().stream()
            .filter(msg -> msg.getMetadata().getImportance() >= 8)
            .collect(Collectors.toList());
    }
}
```

#### 5.5.3 压缩流程图

```
原始上下文（8000 tokens）
    ↓
识别重要信息（决策、代码块、结论）
    ↓
LLM 生成摘要（500 tokens）
    ↓
构建新上下文：最近5轮 + 摘要 + 重要信息（2400 tokens）
```

### 5.6 会话恢复

#### 5.6.1 会话状态持久化

```java
/**
 * 会话状态
 */
@Data
@Builder
public class SessionState {
    private String conversationId;        // 会话ID
    private String userId;                // 用户ID
    private List<Message> messages;       // 消息历史
    private TaskPlan currentPlan;         // 当前执行计划
    private List<SubTask> pendingTasks;   // 待执行任务
    private Map<String, Object> variables; // 会话变量
    private LocalDateTime createdAt;      // 创建时间
    private LocalDateTime lastActiveAt;   // 最后活跃时间
    private SessionStatus status;         // 状态
}

/**
 * 会话状态持久化服务
 */
@Service
public class SessionPersistenceService {

    private final SessionStateRepository repository;

    /**
     * 保存会话状态
     */
    public void saveSession(String conversationId, AgentContext context) {
        SessionState state = SessionState.builder()
            .conversationId(conversationId)
            .userId(context.getUserId())
            .messages(context.getMessages())
            .currentPlan(context.getCurrentPlan())
            .pendingTasks(context.getPendingTasks())
            .variables(context.getVariables())
            .createdAt(context.getCreatedAt())
            .lastActiveAt(LocalDateTime.now())
            .status(SessionStatus.ACTIVE)
            .build();

        repository.save(state);
    }

    /**
     * 恢复会话
     */
    public AgentContext restoreSession(String conversationId) {
        SessionState state = repository.findById(conversationId)
            .orElse(null);

        if (state == null || state.getStatus() != SessionStatus.ACTIVE) {
            return null;
        }

        return AgentContext.builder()
            .conversationId(state.getConversationId())
            .userId(state.getUserId())
            .messages(new ArrayList<>(state.getMessages()))
            .currentPlan(state.getCurrentPlan())
            .pendingTasks(new ArrayList<>(state.getPendingTasks()))
            .variables(new HashMap<>(state.getVariables()))
            .createdAt(state.getCreatedAt())
            .build();
    }

    /**
     * 获取用户未完成的会话
     */
    public List<SessionState> getIncompleteSessions(String userId) {
        return repository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE);
    }
}
```

#### 5.6.2 会话恢复流程

```
用户打开页面
    ↓
检查是否有未完成的会话
    ↓
有 → 显示恢复提示
    "您有一个未完成的任务：生成用户管理功能（进度 67%）"
    [继续] [开始新对话]
    ↓
选择继续 → 加载会话状态 → 恢复上下文 → 继续执行
```

#### 5.6.3 数据库表设计

```sql
CREATE TABLE ai_session_state (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    context JSON COMMENT '会话上下文(JSON)',
    current_plan_id VARCHAR(64) COMMENT '当前执行计划ID',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态:ACTIVE/COMPLETED/EXPIRED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_active_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    UNIQUE KEY uk_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_last_active (last_active_at)
) COMMENT 'AI会话状态表';
```

---

## 6. 模型层设计

### 6.1 多模型支持

```java
public interface LlmModel {

    /**
     * 模型标识
     */
    String getModelId();

    /**
     * 同步调用
     */
    LlmResponse chat(LlmRequest request);

    /**
     * 流式调用
     */
    Flux<LlmResponse> chatStream(LlmRequest request);

    /**
     * 带工具调用的对话
     */
    Flux<LlmResponse> chatWithTools(LlmRequest request, List<ToolDescriptor> tools);

    /**
     * 获取 Embedding 向量
     */
    float[] embed(String text);

    /**
     * Token 计数
     */
    int countTokens(String text);
}
```

### 6.2 模型配置

```yaml
ai:
  models:
    # 智谱 GLM（默认）
    zhipu:
      enabled: true
      api-key: ${ZHIPU_API_KEY:}
      model: glm-5
      base-url: https://open.bigmodel.cn/api/paas/v4
      max-tokens: 4096
      temperature: 0.7

    # Claude（可选）
    claude:
      enabled: false
      api-key: ${ANTHROPIC_API_KEY:}
      model: claude-sonnet-4-6
      base-url: https://api.anthropic.com
      max-tokens: 8192

    # OpenAI（可选）
    openai:
      enabled: false
      api-key: ${OPENAI_API_KEY:}
      model: gpt-4o
      base-url: https://api.openai.com
      max-tokens: 4096

    # Ollama 本地模型（可选）
    ollama:
      enabled: false
      base-url: http://localhost:11434
      model: llama3
      max-tokens: 4096

  # 默认模型
  default-model: zhipu

  # 模型路由规则
  routing:
    - pattern: "代码.*|编程.*|debug.*"
      model: claude
    - pattern: "中文.*|翻译.*"
      model: zhipu
```

### 6.3 模型路由

```java
@Component
public class ModelRouter {

    /**
     * 根据请求内容选择最佳模型
     */
    public LlmModel selectModel(AgentRequest request) {
        // 1. 检查是否指定了模型
        if (request.getModelId() != null) {
            return modelRegistry.get(request.getModelId());
        }

        // 2. 根据 Agent 类型选择
        if (request.getAgentType() == AgentType.CODE) {
            return modelRegistry.get("claude");
        }

        // 3. 根据内容模式匹配
        for (RoutingRule rule : routingRules) {
            if (rule.matches(request.getMessage())) {
                return modelRegistry.get(rule.getModelId());
            }
        }

        // 4. 返回默认模型
        return modelRegistry.getDefault();
    }
}
```

---

## 7. Agent 执行流程

### 7.1 DevAgent 执行流程

```
┌──────────────────────────────────────────────────────────────────────┐
│                        DevAgent Execution Flow                       │
└──────────────────────────────────────────────────────────────────────┘

用户输入: "帮我做一个用户管理功能"

    │
    ▼
┌─────────────────┐
│  1. 接收请求    │  DevAgent.receive()
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  2. 加载记忆    │  搜索相关历史对话
│                 │  找到：之前讨论过的用户相关需求
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  3. 构建提示    │  系统提示词 + 用户消息 + 记忆 + 工具描述
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  4. 调用模型    │  发送给智谱 GLM
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  5. 解析响应                                                 │
│                                                              │
│  LLM 返回:                                                   │
│  {                                                           │
│    "thought": "需要先了解现有的用户模块结构",                  │
│    "tool_calls": [                                           │
│      {                                                       │
│        "tool": "project_reader",                             │
│        "arguments": {"action": "structure"}                  │
│      },                                                      │
│      {                                                       │
│        "tool": "code_search",                                │
│        "arguments": {"type": "class", "query": "User"}       │
│      }                                                       │
│    ]                                                         │
│  }                                                           │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐
│  6. 执行工具    │  ProjectReaderTool → 获取项目结构
│                 │  CodeSearchTool → 找到 User.java, UserService.java
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  7. 整合结果    │  将工具结果加入上下文，再次调用 LLM
│                 │  LLM 分析现有代码结构
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│  8. 生成文档                                                  │
│                                                              │
│  LLM 返回:                                                   │
│  {                                                           │
│    "thought": "基于现有架构，我来生成 PRD 和技术方案",         │
│    "tool_calls": [                                           │
│      {                                                       │
│        "tool": "file_writer",                                │
│        "arguments": {                                        │
│          "path": "docs/prd-user-management.md",             │
│          "content": "# 用户管理功能 PRD...",                  │
│          "action": "create"                                  │
│        }                                                     │
│      }                                                       │
│    ]                                                         │
│  }                                                           │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐
│  9. 生成代码    │  file_writer 创建 Controller/Service/Vue
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  10. 保存记忆   │  保存对话到向量记忆
│                 │  类型：CHAT（对话）+ DOCUMENT（PRD）+ CODE（代码）
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  11. 返回用户   │  流式输出：PRD内容 + 代码路径 + 下一步建议
└─────────────────┘
```

### 7.2 工具调用循环

```
while (response.hasToolCalls()) {
    for (ToolCall call : response.getToolCalls()) {
        // 1. 检查权限
        if (tool.requireConfirmation() && !userConfirm(call)) {
            continue;
        }

        // 2. 参数校验
        validateParameters(call);

        // 3. 执行工具
        ToolResult result = toolExecutor.execute(call);

        // 4. 将结果加入上下文
        context.addToolResult(call.getId(), result);
    }

    // 5. 再次调用模型
    response = model.chat(context.toRequest());
}
```

### 7.3 进度追踪

#### 7.3.1 进度信息模型

```java
/**
 * 进度信息
 */
@Data
@Builder
public class Progress {
    private String planId;            // 计划ID
    private int currentStep;          // 当前步骤
    private int totalSteps;           // 总步骤数
    private int percentage;           // 完成百分比 0-100
    private String currentTask;       // 当前任务描述
    private String status;            // 状态: RUNNING/COMPLETED/ERROR
    private String message;           // 进度消息
    private LocalDateTime timestamp;  // 时间戳
}

/**
 * 进度追踪服务
 */
@Service
public class ProgressTracker {

    private final WebSocketService webSocketService;

    /**
     * 更新进度
     */
    public void updateProgress(String planId, int step, int totalSteps,
                               String status, String message) {
        Progress progress = Progress.builder()
            .planId(planId)
            .currentStep(step)
            .totalSteps(totalSteps)
            .status(status)
            .message(message)
            .percentage((step * 100) / totalSteps)
            .timestamp(LocalDateTime.now())
            .build();

        // 推送给前端
        webSocketService.sendProgress(userId, progress);
    }

    /**
     * 标记任务开始
     */
    public void startTask(String planId, String taskDescription) {
        updateProgress(planId, 0, 0, "RUNNING", "开始执行: " + taskDescription);
    }

    /**
     * 标记步骤完成
     */
    public void completeStep(String planId, int step, int totalSteps, String stepName) {
        updateProgress(planId, step, totalSteps, "RUNNING",
            "完成步骤 [" + step + "/" + totalSteps + "]: " + stepName);
    }

    /**
     * 标记任务完成
     */
    public void completeTask(String planId) {
        updateProgress(planId, 0, 0, "COMPLETED", "任务已完成");
    }

    /**
     * 标记错误
     */
    public void error(String planId, String errorMessage) {
        updateProgress(planId, 0, 0, "ERROR", "执行出错: " + errorMessage);
    }
}
```

#### 7.3.2 前端进度展示

```
┌─────────────────────────────────────────────────────────────┐
│  📊 任务进度                                                 │
│  ─────────────────────────────────────────────────────────  │
│  总目标：做一个用户管理功能                                   │
│  进度：4/6 (67%)                                             │
│  ████████████████░░░░░░░░                                   │
│                                                              │
│  ✅ [1] 分析现有用户模块                                     │
│  ✅ [2] 生成 PRD 文档                                        │
│  ✅ [3] 设计 API 接口                                        │
│  ✅ [4] 生成后端代码                                         │
│  🔄 [5] 生成前端页面... (正在进行)                           │
│  ⏳ [6] 编写测试用例                                         │
└─────────────────────────────────────────────────────────────┘
```

#### 7.3.3 WebSocket 进度推送

```java
/**
 * WebSocket 进度推送
 */
@Controller
public class ProgressWebSocketController {

    @MessageMapping("/progress/{planId}")
    @SendTo("/topic/progress/{planId}")
    public Progress pushProgress(Progress progress) {
        return progress;
    }
}
```

#### 7.3.4 数据库表设计

```sql
CREATE TABLE ai_task_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id VARCHAR(64) NOT NULL COMMENT '计划ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) COMMENT '会话ID',
    goal TEXT NOT NULL COMMENT '总目标',
    sub_tasks JSON COMMENT '子任务列表(JSON)',
    current_step INT DEFAULT 0 COMMENT '当前步骤',
    total_steps INT COMMENT '总步骤数',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态:PENDING/RUNNING/COMPLETED/ERROR',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_plan_id (plan_id),
    INDEX idx_user_id (user_id),
    INDEX idx_conversation_id (conversation_id)
) COMMENT 'AI任务计划表';
```

---

## 8. API 接口设计

### 8.1 对话接口

```
POST /api/ai/chat
Content-Type: application/json

请求体：
{
    "message": "帮我分析上个月的财务数据",
    "conversationId": "conv-123",          // 可选
    "agentType": "data",                   // 可选：chat/code/data/finance
    "modelId": "zhipu",                    // 可选：指定模型
    "stream": true                         // 是否流式输出
}

响应（流式）：
event: thought
data: {"content": "我需要先查询数据库..."}

event: tool_call
data: {"tool": "database_query", "status": "executing"}

event: tool_result
data: {"tool": "database_query", "result": "查询到 15 条记录"}

event: message
data: {"content": "根据查询结果，上个月..."}

event: done
data: {"conversationId": "conv-123"}
```

### 8.2 Agent 管理接口

```
# 获取可用 Agent 列表
GET /api/ai/agents

响应：
{
    "agents": [
        {"id": "chat", "name": "通用助手", "tools": []},
        {"id": "data", "name": "数据分析", "tools": ["database_query", "calculator"]}
    ]
}

# 获取 Agent 详情
GET /api/ai/agents/{agentId}

# 创建自定义 Agent
POST /api/ai/agents
{
    "name": "财务助手",
    "description": "专注于财务分析",
    "modelId": "zhipu",
    "tools": ["database_query", "calculator", "finance_report"],
    "systemPrompt": "你是一个专业的财务分析师..."
}
```

### 8.3 工具管理接口

```
# 获取可用工具列表
GET /api/ai/tools

响应：
{
    "tools": [
        {
            "name": "database_query",
            "description": "执行 SQL 查询",
            "requireConfirmation": false
        },
        {
            "name": "api_call",
            "description": "调用外部 API",
            "requireConfirmation": true
        }
    ]
}

# 执行工具（手动调用）
POST /api/ai/tools/{toolName}/execute
{
    "parameters": {...}
}
```

### 8.4 会话管理接口

```
# 获取会话历史
GET /api/ai/conversations/{conversationId}/history

# 清空会话
DELETE /api/ai/conversations/{conversationId}

# 获取用户所有会话
GET /api/ai/conversations
```

---

## 9. 前端界面设计

### 9.1 对话界面

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  DevAgent 开发助手                                    [清空] [历史] [设置]  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ 🤖 DevAgent                                          [复制] [重新生成]│   │
│  │                                                                       │   │
│  │ 💭 我来帮你做用户管理功能，先了解一下项目结构...                      │   │
│  │                                                                       │   │
│  │ 🔧 调用工具: project_reader                                          │   │
│  │    └─ action: structure                                              │   │
│  │    └─ ✅ 获取到项目模块结构                                          │   │
│  │                                                                       │   │
│  │ 🔧 调用工具: code_search                                             │   │
│  │    └─ type: class, query: User                                       │   │
│  │    └─ ✅ 找到 User.java, UserService.java, UserController.java       │   │
│  │                                                                       │   │
│  │ 📄 生成文档: docs/prd-user-management.md                             │   │
│  │    └─ ✅ PRD 已创建                                                  │   │
│  │                                                                       │   │
│  │ 📝 生成代码:                                                          │   │
│  │    └─ ✅ UserController.java - 用户接口                              │   │
│  │    └─ ✅ UserService.java - 用户服务                                 │   │
│  │    └─ ✅ UserManage.vue - 前端页面                                   │   │
│  │                                                                       │   │
│  │ 🎉 **用户管理功能已完成！**                                          │   │
│  │                                                                       │   │
│  │ 接下来你可以：                                                        │   │
│  │ 1. 查看生成的 PRD 文档                                               │   │
│  │ 2. 运行项目测试接口                                                   │   │
│  │ 3. 让我继续完善其他功能                                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│                    ┌────────────────────────────────────────────┐          │
│                    │ 👤 用户                                    │          │
│                    │ 帮我做一个用户管理功能                      │          │
│                    └────────────────────────────────────────────┘          │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐  [发送]  │
│  │ 描述你的需求...                                             │           │
│  └─────────────────────────────────────────────────────────────┘           │
│                                                                             │
│  💡 快捷操作: [📄 生成PRD] [💻 写代码] [🔍 查代码] [📖 查文档]             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 工具执行展示

```
┌─────────────────────────────────────────────────────────────┐
│  🔧 工具调用                                                 │
│  ─────────────────────────────────────────────────────────  │
│  工具: code_search                                          │
│  状态: ✅ 执行成功                                           │
│  耗时: 0.15s                                                │
│  ─────────────────────────────────────────────────────────  │
│  参数:                                                      │
│  {                                                          │
│    "type": "class",                                         │
│    "query": "User"                                          │
│  }                                                          │
│  ─────────────────────────────────────────────────────────  │
│  结果:                                                      │
│  • finance-datasource/.../User.java                         │
│  • finance-datasource/.../UserService.java                  │
│  • finance-application/.../UserController.java              │
└─────────────────────────────────────────────────────────────┘
```

### 9.3 文件生成展示

```
┌─────────────────────────────────────────────────────────────┐
│  📄 文件已生成                                               │
│  ─────────────────────────────────────────────────────────  │
│  路径: docs/prd-user-management.md                          │
│  操作: 创建                                                  │
│  大小: 2.3 KB                                               │
│  ─────────────────────────────────────────────────────────  │
│  [查看内容] [编辑] [在IDE中打开]                             │
└─────────────────────────────────────────────────────────────┘
```

### 9.4 前端组件结构

```
views/ai/
├── index.vue              # 对话主页面
└── components/
    ├── ChatContainer.vue  # 对话容器
    ├── ChatMessage.vue    # 消息组件（用户/AI）
    ├── ChatInput.vue      # 输入框
    ├── ToolCallCard.vue   # 工具调用展示卡片
    ├── FileCard.vue       # 文件生成展示卡片
    ├── CodeBlock.vue      # 代码块（带高亮）
    └── MarkdownRender.vue # Markdown 渲染
```

│ 工具: database_query │
│ 状态: ✅ 执行成功 │
│ 耗时: 0.23s │
│ ───────────────────────────────────────────────────────── │
│ 参数:                                                      │
│ { │
│    "sql": "SELECT category, SUM(amount)..."                │
│ } │
│ ───────────────────────────────────────────────────────── │
│ 结果: (点击展开)                                            │
│  [+ 展开详细数据]                                           │
└─────────────────────────────────────────────────────────────┘

```

---

## 10. 安全设计

### 10.1 工具安全

| 安全措施 | 说明 |
|---------|------|
| **沙箱执行** | 文件操作限定在安全目录内 |
| **SQL 注入防护** | 仅允许 SELECT 查询，禁止 INSERT/UPDATE/DELETE |
| **API 白名单** | 仅允许调用预定义的 API 域名 |
| **敏感操作确认** | 危险工具需要用户确认才能执行 |
| **执行超时** | 工具执行超时自动终止 |
| **日志审计** | 所有工具调用记录日志 |

### 10.2 Prompt 注入防护
```java
@Component
public class PromptInjectionDetector {

    private static final List<String> DANGEROUS_PATTERNS = List.of(
        "ignore previous instructions",
        "system prompt",
        "你现在是",
        "forget everything",
        "###",
        "---"
    );

    public DetectionResult detect(String input) {
        for (String pattern : DANGEROUS_PATTERNS) {
            if (input.toLowerCase().contains(pattern)) {
                return DetectionResult.dangerous(pattern);
            }
        }
        return DetectionResult.safe();
    }
}
```

### 10.3 权限控制

```java
@PreAuthorize("hasPermission('ai', 'chat')")
public AgentResponse execute(AgentRequest request) {
    // ...
}

// 工具级别权限
@ToolPermission("database_query")
public class DatabaseTool implements Tool {
    // 只有有数据库查询权限的用户才能使用
}
```

### 10.4 敏感数据保护

#### 10.4.1 敏感数据识别与脱敏

```java
/**
 * 敏感数据检测器
 */
@Service
public class SensitiveDataDetector {

    // 敏感数据模式
    private static final List<SensitivePattern> PATTERNS = List.of(
        new SensitivePattern("PHONE", Pattern.compile("1[3-9]\\d{9}"), true),
        new SensitivePattern("ID_CARD", Pattern.compile("\\d{17}[\\dXx]"), true),
        new SensitivePattern("BANK_CARD", Pattern.compile("\\d{16,19}"), true),
        new SensitivePattern("EMAIL", Pattern.compile("[\\w.-]+@[\\w.-]+\\.\\w+"), false),
        new SensitivePattern("MONEY", Pattern.compile("[¥$]\\d+(\\.\\d{2})?|\\d+(\\.\\d{2})?[元万美元]"), true),
        new SensitivePattern("PASSWORD", Pattern.compile("(password|pwd|密码)[=:：]\\s*\\S+"), true),
        new SensitivePattern("API_KEY", Pattern.compile("(api[_-]?key|apikey)[=:：]\\s*\\S+"), true)
    );

    /**
     * 检测文本中的敏感数据
     */
    public List<SensitiveDataMatch> detect(String text) {
        List<SensitiveDataMatch> matches = new ArrayList<>();
        for (SensitivePattern pattern : PATTERNS) {
            Matcher matcher = pattern.getPattern().matcher(text);
            while (matcher.find()) {
                matches.add(new SensitiveDataMatch(
                    pattern.getType(),
                    matcher.group(),
                    matcher.start(),
                    matcher.end(),
                    pattern.isNeedsMasking()
                ));
            }
        }
        return matches;
    }

    /**
     * 脱敏处理
     */
    public String mask(String text) {
        String masked = text;
        for (SensitivePattern pattern : PATTERNS) {
            if (pattern.isNeedsMasking()) {
                Matcher matcher = pattern.getPattern().matcher(masked);
                masked = matcher.replaceAll(m -> maskValue(m.group(), pattern.getType()));
            }
        }
        return masked;
    }

    private String maskValue(String value, String type) {
        return switch (type) {
            case "PHONE" -> value.substring(0, 3) + "****" + value.substring(7);
            case "ID_CARD" -> value.substring(0, 6) + "********" + value.substring(14);
            case "BANK_CARD" -> "****" + value.substring(value.length() - 4);
            case "MONEY" -> "****";
            case "PASSWORD", "API_KEY" -> "[已脱敏]";
            default -> "***";
        };
    }
}
```

#### 10.4.2 安全审计日志

```java
/**
 * 安全审计服务
 */
@Service
public class SecurityAuditService {

    /**
     * 记录敏感操作
     */
    public void logSensitiveOperation(SecurityAuditEvent event) {
        SecurityAuditLog log = SecurityAuditLog.builder()
            .userId(event.getUserId())
            .operation(event.getOperation())
            .resourceType(event.getResourceType())
            .sensitiveDataCount(event.getSensitiveDataCount())
            .ipAddress(event.getIpAddress())
            .timestamp(LocalDateTime.now())
            .build();

        repository.save(log);
    }

    /**
     * 检查异常行为
     */
    public List<SecurityAlert> checkAnomalies(String userId) {
        List<SecurityAlert> alerts = new ArrayList<>();

        // 1小时内敏感数据访问超过 100 次
        long recentCount = repository.countByUserIdAndTimestampAfter(
            userId, LocalDateTime.now().minusHours(1)
        );
        if (recentCount > 100) {
            alerts.add(new SecurityAlert("HIGH_FREQUENCY_ACCESS",
                "1小时内访问敏感数据 " + recentCount + " 次"));
        }

        return alerts;
    }
}
```

### 10.5 人机协作增强

#### 10.5.1 确认请求类型

```java
/**
 * 确认请求类型
 */
public enum ConfirmationType {
    FILE_WRITE,        // 写入文件
    FILE_DELETE,       // 删除文件
    CONTINUE_PLAN,     // 继续执行计划
    ROLLBACK,          // 回滚操作
    SENSITIVE_DATA     // 涉及敏感数据
}
```

#### 10.5.2 人机协作服务

```java
/**
 * 确认请求
 */
@Data
@Builder
public class ConfirmationRequest {
    private String requestId;            // 请求ID
    private String conversationId;       // 会话ID
    private ConfirmationType type;       // 确认类型
    private String message;             // 提示消息
    private Map<String, Object> details; // 详细信息
    private Duration timeout;            // 超时时间
}

/**
 * 人机协作服务
 */
@Service
public class HumanLoopService {

    private final WebSocketService webSocketService;
    private final Map<String, CompletableFuture<Boolean>> pendingConfirmations = new ConcurrentHashMap<>();

    /**
     * 请求用户确认
     */
    public CompletableFuture<Boolean> requestConfirmation(
            String conversationId,
            ConfirmationType type,
            String message,
            Map<String, Object> details) {

        ConfirmationRequest request = ConfirmationRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .conversationId(conversationId)
            .type(type)
            .message(message)
            .details(details)
            .timeout(Duration.ofMinutes(5))
            .build();

        // 推送给前端
        webSocketService.sendConfirmationRequest(getUserId(conversationId), request);

        // 等待用户响应
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingConfirmations.put(request.getRequestId(), future);

        // 设置超时
        future.orTimeout(5, TimeUnit.MINUTES, () -> {
            pendingConfirmations.remove(request.getRequestId());
        });

        return future;
    }

    /**
     * 用户响应确认
     */
    public void handleConfirmationResponse(String requestId, boolean approved, String comment) {
        CompletableFuture<Boolean> future = pendingConfirmations.remove(requestId);
        if (future != null) {
            future.complete(approved);
        }
    }

    /**
     * 检查是否需要确认
     */
    public boolean requiresConfirmation(ToolCall call, Tool tool) {
        // 危险工具需要确认
        if (tool.requireConfirmation()) {
            return true;
        }

        // 文件写入操作需要确认
        if ("file_writer".equals(call.getTool())) {
            return true;
        }

        // 涉及敏感数据需要确认
        if (containsSensitiveData(call.getArguments())) {
            return true;
        }

        return false;
    }
}
```

#### 10.5.3 前端确认交互

```
┌─────────────────────────────────────────────────────────────┐
│  ⚠️ 确认请求                                                 │
│                                                              │
│  Agent 准备执行以下操作：                                     │
│                                                              │
│  📝 写入文件: UserController.java                            │
│  📝 写入文件: UserService.java                              │
│                                                              │
│  [✓ 确认执行]  [✗ 拒绝]  [查看详情]                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 11. 错误处理体系

> 注意：全局异常处理器已在现有项目中实现，此处仅定义 Agent 模块特有的异常类型。

### 11.1 Agent 异常层次结构

```java
/**
 * Agent 异常基类
 * 所有 Agent 相关异常都继承此类，便于统一处理
 */
public class AgentException extends RuntimeException {
    /** 错误码，用于前端识别错误类型 */
    private final String errorCode;
    /** 错误级别，决定处理策略 */
    private final ErrorLevel level;

    /**
     * 错误级别枚举
     */
    public enum ErrorLevel {
        WARNING,    // 警告：可恢复，继续执行
        ERROR,      // 错误：需要重试
        FATAL       // 致命：无法恢复，终止执行
    }

    public AgentException(String errorCode, String message, ErrorLevel level) {
        super(message);
        this.errorCode = errorCode;
        this.level = level;
    }

    public AgentException(String errorCode, String message, ErrorLevel level, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.level = level;
    }

    // Getter 方法
    public String getErrorCode() { return errorCode; }
    public ErrorLevel getLevel() { return level; }
}

/**
 * 模型相关异常
 * 处理 LLM 模型调用过程中的各种错误
 */
public class ModelException extends AgentException {
    /** 模型响应超时 */
    public static final String TIMEOUT = "MODEL_TIMEOUT";
    /** 请求频率超限 */
    public static final String RATE_LIMIT = "MODEL_RATE_LIMIT";
    /** 上下文长度超限 */
    public static final String CONTEXT_OVERFLOW = "MODEL_CONTEXT_OVERFLOW";
    /** 模型服务不可用 */
    public static final String UNAVAILABLE = "MODEL_UNAVAILABLE";

    public ModelException(String errorCode, String message) {
        super(errorCode, message, ErrorLevel.ERROR);
    }
}

/**
 * 工具相关异常
 * 处理工具调用过程中的各种错误
 */
public class ToolException extends AgentException {
    /** 工具未找到 */
    public static final String NOT_FOUND = "TOOL_NOT_FOUND";
    /** 参数校验失败 */
    public static final String INVALID_PARAMS = "TOOL_INVALID_PARAMS";
    /** 工具执行失败 */
    public static final String EXECUTION_FAILED = "TOOL_EXECUTION_FAILED";
    /** 权限不足 */
    public static final String PERMISSION_DENIED = "TOOL_PERMISSION_DENIED";
    /** 执行超时 */
    public static final String TIMEOUT = "TOOL_TIMEOUT";

    public ToolException(String errorCode, String message) {
        super(errorCode, message, ErrorLevel.ERROR);
    }
}

/**
 * 记忆相关异常
 * 处理记忆系统中的各种错误
 */
public class MemoryException extends AgentException {
    /** 记忆容量超限 */
    public static final String OVERFLOW = "MEMORY_OVERFLOW";
    /** 记忆未找到 */
    public static final String NOT_FOUND = "MEMORY_NOT_FOUND";
    /** 持久化失败 */
    public static final String PERSISTENCE_FAILED = "MEMORY_PERSISTENCE_FAILED";

    public MemoryException(String errorCode, String message) {
        super(errorCode, message, ErrorLevel.ERROR);
    }
}

/**
 * 安全相关异常
 * 处理安全检测中的各种问题
 */
public class SecurityException extends AgentException {
    /** 检测到 Prompt 注入攻击 */
    public static final String PROMPT_INJECTION = "SECURITY_PROMPT_INJECTION";
    /** 涉及敏感数据 */
    public static final String SENSITIVE_DATA = "SECURITY_SENSITIVE_DATA";
    /** 未授权访问 */
    public static final String UNAUTHORIZED = "SECURITY_UNAUTHORIZED";

    public SecurityException(String errorCode, String message) {
        super(errorCode, message, ErrorLevel.FATAL);  // 安全问题默认为致命级别
    }
}
```

### 11.2 异常处理策略

```java
/**
 * Agent 异常处理策略接口
 * 可根据异常类型和级别选择不同的处理策略
 */
public interface AgentExceptionStrategy {
    /**
     * 判断是否能处理该异常
     */
    boolean canHandle(AgentException exception);

    /**
     * 处理异常
     * @return 是否已处理，true=继续执行，false=终止执行
     */
    boolean handle(AgentException exception, AgentContext context);
}

/**
 * 默认异常处理策略
 */
@Component
public class DefaultExceptionStrategy implements AgentExceptionStrategy {

    @Override
    public boolean canHandle(AgentException exception) {
        return true;  // 处理所有异常
    }

    @Override
    public boolean handle(AgentException exception, AgentContext context) {
        // 根据错误级别决定处理方式
        switch (exception.getLevel()) {
            case WARNING:
                // 警告级别：记录日志，继续执行
                log.warn("Agent warning: {}", exception.getMessage());
                return true;

            case ERROR:
                // 错误级别：尝试重试或恢复
                log.error("Agent error: {}", exception.getMessage());
                return attemptRecovery(exception, context);

            case FATAL:
                // 致命级别：终止执行
                log.error("Agent fatal error: {}", exception.getMessage());
                return false;

            default:
                return false;
        }
    }

    /**
     * 尝试恢复
     */
    private boolean attemptRecovery(AgentException exception, AgentContext context) {
        // 模型超时：可以重试
        if (exception instanceof ModelException) {
            return context.getRetryCount() < 3;
        }
        // 工具错误：可以选择跳过或使用替代工具
        if (exception instanceof ToolException) {
            return attemptToolRecovery((ToolException) exception, context);
        }
        return false;
    }
}
```

### 11.3 重试机制

```java
/**
 * 带重试的执行器
 * 支持指数退避的重试策略
 */
@Service
public class RetryExecutor {

    /** 最大重试次数 */
    private static final int MAX_RETRIES = 3;
    /** 初始重试延迟（毫秒） */
    private static final long INITIAL_DELAY_MS = 1000;
    /** 最大重试延迟（毫秒） */
    private static final long MAX_DELAY_MS = 10000;

    public <T> T executeWithRetry(Supplier<T> action, String operationName) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                return action.get();
            } catch (ModelException e) {
                lastException = e;
                if (e.getErrorCode().equals(ModelException.RATE_LIMIT)) {
                    // 限流时等待更长时间
                    sleep(RETRY_DELAY_MS * (attempt + 1) * 2);
                } else if (e.getErrorCode().equals(ModelException.TIMEOUT)) {
                    sleep(RETRY_DELAY_MS);
                } else {
                    throw e; // 其他错误不重试
                }
                attempt++;
            }
        }

        throw new AgentException("RETRY_EXHAUSTED",
            "操作 " + operationName + " 重试 " + MAX_RETRIES + " 次后仍失败",
            ErrorLevel.FATAL, lastException);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
```

---

## 12. Token 限流与计费

### 12.1 Token 追踪服务

```java
/**
 * Token 使用追踪
 */
@Service
public class TokenTracker {

    /**
     * 记录 Token 消耗
     */
    public void record(String conversationId, String userId, TokenUsage usage) {
        TokenUsageRecord record = TokenUsageRecord.builder()
            .conversationId(conversationId)
            .userId(userId)
            .modelId(usage.getModelId())
            .inputTokens(usage.getInputTokens())
            .outputTokens(usage.getOutputTokens())
            .totalTokens(usage.getTotalTokens())
            .timestamp(LocalDateTime.now())
            .build();

        repository.save(record);
    }

    /**
     * 获取用户统计
     */
    public TokenStats getStats(String userId, LocalDate start, LocalDate end) {
        List<TokenUsageRecord> records = repository.findByUserIdAndDateBetween(userId, start, end);

        return TokenStats.builder()
            .totalTokens(records.stream().mapToLong(TokenUsageRecord::getTotalTokens).sum())
            .byDate(groupByDate(records))
            .build();
    }
}
```

### 12.2 多级限流服务

```java
/**
 * 多级限流
 */
@Service
public class RateLimiter {

    private final RedisTemplate<String, String> redis;

    // 用户级限流：每分钟 20 次
    private static final int USER_RPM = 20;

    // 用户级限流：每天 100000 tokens
    private static final long USER_DAILY_TOKENS = 100_000;

    // 系统级限流：每分钟 1000 次（最大并发 3）
    private static final int SYSTEM_RPM = 1000;
    private static final int MAX_CONCURRENT = 3;

    /**
     * 检查是否允许请求
     */
    public RateLimitResult checkLimit(String userId) {
        // 1. 检查并发数
        int currentConcurrent = getCurrentConcurrent(userId);
        if (currentConcurrent >= MAX_CONCURRENT) {
            return RateLimitResult.denied("CONCURRENT_EXCEEDED",
                "当前有 " + currentConcurrent + " 个请求在处理中，请等待");
        }

        // 2. 检查用户每分钟请求限制
        String userRpmKey = "rate_limit:rpm:user:" + userId;
        if (!incrementAndCheck(userRpmKey, 60, USER_RPM)) {
            return RateLimitResult.denied("USER_RPM_EXCEEDED", "请求过于频繁，请稍后重试");
        }

        // 3. 检查用户每日 Token 限制
        String userDailyKey = "rate_limit:daily:user:" + userId + ":" + LocalDate.now();
        long todayTokens = getTokenCount(userDailyKey);
        if (todayTokens >= USER_DAILY_TOKENS) {
            return RateLimitResult.denied("USER_DAILY_EXCEEDED", "今日 Token 额度已用尽");
        }

        return RateLimitResult.allowed();
    }

    /**
     * 预扣 Token
     */
    public void reserveTokens(String userId, long tokens) {
        String userDailyKey = "rate_limit:daily:user:" + userId + ":" + LocalDate.now();
        redis.opsForValue().increment(userDailyKey, tokens);
    }
}
```

### 12.3 限流配置

```yaml
ai:
  rate-limit:
    # 用户级
    user:
      rpm: 20                    # 每分钟请求数
      daily-tokens: 100000       # 每日 Token 限额
      max-concurrent: 3          # 最大并发数

    # 系统级
    system:
      rpm: 1000                  # 系统每分钟总请求数

    # 重试配置
    retry:
      max-attempts: 3
      delay-ms: 1000
      multiplier: 2              # 延迟倍增因子
```

---

## 13. 配置说明

### 13.1 完整配置

```yaml
# ============================================================
# AI Agent 智能体框架配置文件
# 所有配置项都有详细注释，请根据实际环境修改
# ============================================================

ai:
  # ============================================================
  # 模型配置 - 定义可用的 LLM 模型
  # ============================================================
  models:
    # 智谱 GLM 模型配置（默认使用）
    zhipu:
      enabled: true                                    # 是否启用该模型
      api-key: ${ZHIPU_API_KEY:}                       # API密钥，从环境变量读取，为空则使用默认值
      model: glm-5                                     # 模型名称：glm-4 / glm-5
      base-url: https://open.bigmodel.cn/api/paas/v4   # API基础地址
      max-tokens: 4096                                 # 单次请求最大Token数
      temperature: 0.7                                 # 温度参数，0-1之间，越高越随机
      timeout: 30000                                   # 请求超时时间（毫秒）
      retry-times: 3                                   # 失败重试次数

    # Claude 模型配置（可选）
    claude:
      enabled: false                                   # 是否启用该模型
      api-key: ${ANTHROPIC_API_KEY:}                   # Anthropic API密钥
      model: claude-sonnet-4-6                         # 模型名称
      base-url: https://api.anthropic.com              # API基础地址
      max-tokens: 8192                                 # 单次请求最大Token数
      temperature: 0.7                                 # 温度参数

    # OpenAI 模型配置（可选）
    openai:
      enabled: false                                   # 是否启用该模型
      api-key: ${OPENAI_API_KEY:}                      # OpenAI API密钥
      model: gpt-4o                                    # 模型名称
      base-url: https://api.openai.com                 # API基础地址
      max-tokens: 4096                                 # 单次请求最大Token数

    # Ollama 本地模型配置（可选）
    ollama:
      enabled: false                                   # 是否启用本地模型
      base-url: http://localhost:11434                 # Ollama服务地址
      model: llama3                                    # 模型名称
      max-tokens: 4096                                 # 单次请求最大Token数

  # ============================================================
  # 默认配置 - 全局默认值
  # ============================================================
  default-model: zhipu                                # 默认使用的模型ID
  default-agent: chat                                 # 默认使用的Agent类型

  # ============================================================
  # Agent 配置 - 定义不同类型的 Agent
  # ============================================================
  agents:
    # 开发助手 Agent 配置
    dev:
      model: zhipu                                     # 使用的模型ID
      tools: [project_reader, file_reader, file_writer, code_search]  # 可用工具列表
      max-context-rounds: 20                           # 最大上下文轮数
      system-prompt: |                                 # 系统提示词
        你是一个全栈开发助手，完全了解当前项目的架构和代码。
        你的职责是理解用户需求，生成符合规范的技术方案和代码。

    # 通用对话 Agent 配置
    chat:
      model: zhipu                                     # 使用的模型ID
      tools: []                                        # 不使用工具，纯对话
      max-context-rounds: 10                           # 最大上下文轮数
      system-prompt: |                                 # 系统提示词
        你是一个友好的对话助手，能够帮助用户解答各种问题。

  # ============================================================
  # 工具配置 - 定义各个工具的行为
  # ============================================================
  tools:
    # 项目读取工具配置
    project_reader:
      enabled: true                                    # 是否启用该工具
      project-root: ${PROJECT_ROOT:./}                 # 项目根目录路径

    # 文件读取工具配置
    file_reader:
      enabled: true                                    # 是否启用该工具
      allowed-extensions: [.java, .xml, .yml, .properties, .vue, .js, .ts, .md]  # 允许读取的文件扩展名
      max-file-size: 1MB                               # 最大文件大小限制

    # 文件写入工具配置
    file_writer:
      enabled: true                                    # 是否启用该工具
      require-confirmation: false                      # 是否需要用户确认后才写入
      allowed-directories: []                          # 允许写入的目录列表，空=不限制

    # 代码搜索工具配置
    code_search:
      enabled: true                                    # 是否启用该工具
      index-on-startup: true                           # 是否在启动时建立索引

  # ============================================================
  # 并发控制 - 限制请求频率和并发数
  # ============================================================
  concurrency:
    max-requests: 3                                    # 最大并发请求数
    queue-size: 10                                     # 等待队列大小
    timeout-seconds: 300                               # 单次请求超时时间（秒）

  # ============================================================
  # 记忆配置 - 配置不同类型的记忆存储
  # ============================================================
  memory:
    # 短期记忆配置（会话级）
    short-term:
      max-messages: 20                                 # 最大保留消息数
      expire-minutes: 60                               # 过期时间（分钟）

    # 长期记忆配置（持久化）
    long-term:
      enabled: true                                    # 是否启用长期记忆
      storage: mysql                                   # 存储类型：mysql / redis

    # 向量记忆配置（语义检索）
    vector:
      enabled: true                                    # 是否启用向量记忆
      embedding-model: zhipu-embedding-3               # 向量嵌入模型
      similarity-threshold: 0.7                        # 相似度阈值，0-1之间
      top-k: 5                                         # 返回最相关的K条记录

    # 上下文压缩配置
    compaction:
      enabled: true                                    # 是否启用自动压缩
      strategy: summary                                # 压缩策略：summary / sliding_window
      max-tokens: 4000                                 # 触发压缩的Token阈值
      target-ratio: 0.3                                # 压缩目标比例

    # 会话恢复配置
    recovery:
      enabled: true                                    # 是否启用会话恢复
      auto-save-interval: 30                           # 自动保存间隔（秒）
      expire-hours: 24                                 # 会话过期时间（小时）

  # ============================================================
  # 安全配置 - 控制安全相关行为
  # ============================================================
  security:
    prompt-injection-check: true                       # 是否启用Prompt注入检测
    tool-confirmation-required: [api_call, file_operation]  # 需要确认的工具类型
    sensitive-data-check: true                         # 是否检测敏感数据
    sensitive-patterns: [phone, id_card, bank_card, email, password, api_key]  # 敏感数据类型

    # 限流配置
    rate-limit:
      enabled: true                                    # 是否启用限流
      requests-per-minute: 20                          # 每分钟最大请求数
      tokens-per-day: 100000                           # 每天最大Token使用量

  # ============================================================
  # 审计配置 - 操作审计和日志
  # ============================================================
  audit:
    enabled: true                                      # 是否启用审计
    log-level: INFO                                    # 日志级别：DEBUG / INFO / WARN / ERROR
    retention-days: 90                                 # 审计日志保留天数
    operations: [tool_call, file_write, model_request] # 需要审计的操作类型

  # ============================================================
  # 进度追踪配置 - 任务执行进度
  # ============================================================
  progress:
    enabled: true                                      # 是否启用进度追踪
    update-interval: 1000                              # 进度更新间隔（毫秒）
    websocket-enabled: true                            # 是否启用WebSocket推送

  # ============================================================
  # 回滚配置 - 文件变更回滚
  # ============================================================
  rollback:
    enabled: true                                      # 是否启用回滚功能
    max-records: 1000                                  # 最大变更记录数
    retention-days: 7                                  # 变更记录保留天数

  # ============================================================
  # 日志配置 - 系统日志行为
  # ============================================================
  logging:
    enabled: true                                      # 是否启用日志
    log-tool-calls: true                               # 是否记录工具调用日志
    log-model-requests: true                           # 是否记录模型请求日志
    sensitive-fields: [api_key, password, token]       # 需要脱敏的字段名
    level: INFO                                        # 日志级别
```

---

## 14. 实施计划

### 12.1 分阶段实施

#### 第一阶段：基础框架 + 对话功能（P0）

- [ ] 模块搭建：module-ai 基础结构
- [ ] 模型层：智谱 GLM 接入（Chat + Embedding）
- [ ] Agent 基础：BaseAgent、DevAgent
- [ ] 对话接口：SSE 流式输出
- [ ] 前端对话界面：Vue 组件

#### 第二阶段：工具系统（P0）

- [ ] Tool 接口和注册机制
- [ ] 内置工具：project_reader、file_reader、file_writer、code_search
- [ ] 工具执行和结果处理
- [ ] 前端工具调用展示

#### 第三阶段：记忆系统（P0）

- [ ] 短期记忆：会话上下文管理
- [ ] 向量记忆：MySQL + 智谱 Embedding
- [ ] 记忆检索和整合
- [ ] 记忆存储时机处理

#### 第四阶段：优化完善（P1）

- [ ] 项目索引：启动时扫描项目结构
- [ ] 错误处理优化
- [ ] 性能优化
- [ ] 多模型支持（可选）

### 12.2 里程碑

| 里程碑 | 目标          | 预计时间  |
|-----|-------------|-------|
| M1  | 基础对话功能可用    | 1 周   |
| M2  | 工具调用功能可用    | 1 周   |
| M3  | 记忆系统可用      | 1 周   |
| M4  | 完整 DevAgent | 3-4 周 |

### 12.3 详细任务清单

#### 第一阶段任务

```
后端：
├── 创建 module-ai 模块
├── 实现智谱 API 客户端
│   ├── ZhipuClient.java - HTTP 客户端
│   ├── ZhipuChatService.java - 对话服务
│   └── ZhipuEmbeddingService.java - 向量服务
├── 实现 Agent 基础
│   ├── Agent.java - 接口
│   ├── BaseAgent.java - 基类
│   └── DevAgent.java - 开发助手
├── 实现对话接口
│   └── AiChatController.java - SSE 流式接口
└── 配置文件

前端：
├── api/ai.js - API 接口
├── views/ai/index.vue - 对话页面
└── components/ai/
    ├── ChatMessage.vue - 消息组件
    ├── ChatInput.vue - 输入组件
    └── ToolCallDisplay.vue - 工具调用展示
```

#### 第二阶段任务

```
后端：
├── tool/Tool.java - 工具接口
├── tool/ToolRegistry.java - 工具注册
├── tool/ToolExecutor.java - 工具执行
└── tool/builtin/
    ├── ProjectReaderTool.java
    ├── FileReaderTool.java
    ├── FileWriterTool.java
    └── CodeSearchTool.java
```

#### 第三阶段任务

```
后端：
├── memory/Memory.java - 记忆接口
├── memory/ShortTermMemory.java - 短期记忆
├── memory/VectorMemory.java - 向量记忆
├── entity/AiMemoryVector.java - 实体
├── mapper/AiMemoryVectorMapper.java - Mapper
└── SQL 脚本
```

---

## 15. 后续扩展

### 15.1 高级功能

- [ ] **RAG 知识库**：接入企业文档，实现知识问答
- [ ] **Multi-Agent 协作**：多个 Agent 协作完成复杂任务
- [ ] **定时任务**：Agent 定时执行任务
- [ ] **Webhook 集成**：外部系统触发 Agent
- [ ] **语音交互**：语音输入输出
- [ ] **多模态**：图片理解、图表生成

### 15.2 运营功能

- [ ] 使用统计 Dashboard
- [ ] Token 消耗分析
- [ ] Agent 效果评估
- [ ] A/B 测试不同 Prompt

---

## 16. 性能优化设计（Claude Code 最佳实践）

### 16.1 前缀缓存架构

#### 16.1.1 缓存策略概述

```java
/**
 * 前缀缓存服务
 * 通过识别请求间的共享前缀，实现 Token 复用
 */
@Service
public class PrefixCacheService {

    /**
     * 缓存命中阈值（前缀最小长度）
     */
    private static final int MIN_PREFIX_LENGTH = 100;

    /**
     * 缓存存储
     */
    private final Cache<String, CacheEntry> cache;

    /**
     * 计算缓存键
     * 基于系统提示词 + 工具描述 + 最近N轮对话的哈希
     */
    public String computeCacheKey(AgentContext context) {
        StringBuilder sb = new StringBuilder();

        // 1. 系统提示词（固定部分）
        sb.append(context.getSystemPrompt());

        // 2. 工具描述（相对固定）
        sb.append(context.getToolDescriptors().stream()
            .map(ToolDescriptor::getSignature)
            .sorted()
            .collect(Collectors.joining()));

        // 3. 最近N轮对话的摘要
        sb.append(computeRecentMessagesHash(context, 5));

        return DigestUtils.md5Hex(sb.toString());
    }

    /**
     * 尝试命中缓存
     */
    public Optional<CacheHit> tryHit(String cacheKey, String newContent) {
        CacheEntry entry = cache.getIfPresent(cacheKey);
        if (entry == null) {
            return Optional.empty();
        }

        // 检查前缀匹配
        int matchLength = computePrefixMatch(entry.getContent(), newContent);
        if (matchLength >= MIN_PREFIX_LENGTH) {
            return Optional.of(new CacheHit(
                entry.getCacheId(),
                matchLength,
                (double) matchLength / newContent.length()
            ));
        }

        return Optional.empty();
    }

    /**
     * 更新缓存
     */
    public void updateCache(String cacheKey, String content, LlmResponse response) {
        CacheEntry entry = CacheEntry.builder()
            .cacheId(UUID.randomUUID().toString())
            .content(content)
            .response(response)
            .createdAt(LocalDateTime.now())
            .hitCount(0)
            .build();

        cache.put(cacheKey, entry);
    }
}
```

#### 16.1.2 缓存统计

```java
/**
 * 缓存统计信息
 */
@Data
public class CacheStats {
    private long totalRequests;           // 总请求数
    private long cacheHits;               // 缓存命中次数
    private double hitRate;               // 命中率
    private long tokensSaved;             // 节省的 Token 数
    private double costSaved;             // 节省的成本（元）
}
```

#### 16.1.3 缓存配置

```yaml
ai:
  cache:
    prefix:
      enabled: true                      # 是否启用前缀缓存
      min-prefix-length: 100             # 最小前缀长度
      max-cache-size: 1000               # 最大缓存条目数
      expire-minutes: 60                 # 缓存过期时间（分钟）
      stats-enabled: true                # 是否启用统计
```

### 16.2 工具执行策略分层

#### 16.2.1 工具分类与权限

```java
/**
 * 工具执行策略
 * 根据工具类型决定执行方式和权限要求
 */
public enum ToolExecutionPolicy {
    /**
     * 只读策略 - 无副作用，直接执行
     */
    READ_ONLY(
        ConfirmationMode.NONE,           // 无需确认
        SandboxMode.NONE,                // 无需沙箱
        true                             // 可并行执行
    ),

    /**
     * 命令策略 - 可能影响系统状态
     */
    COMMAND(
        ConfirmationMode.ASK,            // 需要询问
        SandboxMode.RESTRICTED,          // 受限沙箱
        false                            // 不可并行
    ),

    /**
     * 文件修改策略 - 持久性修改
     */
    FILE_MODIFY(
        ConfirmationMode.REQUIRED,       // 必须确认
        SandboxMode.STRICT,              // 严格沙箱
        false                            // 不可并行
    );

    private final ConfirmationMode confirmationMode;
    private final SandboxMode sandboxMode;
    private final boolean parallelAllowed;
}

/**
 * 确认模式
 */
public enum ConfirmationMode {
    NONE,       // 无需确认
    ASK,        // 询问用户（可跳过）
    REQUIRED    // 必须确认（不可跳过）
}
```

#### 16.2.2 工具执行策略工厂

```java
/**
 * 工具执行策略工厂
 */
@Component
public class ToolExecutionPolicyFactory {

    private final Map<String, ToolExecutionPolicy> toolPolicies = Map.of(
        // 只读工具
        "file_reader", ToolExecutionPolicy.READ_ONLY,
        "code_search", ToolExecutionPolicy.READ_ONLY,
        "project_reader", ToolExecutionPolicy.READ_ONLY,
        "database_query", ToolExecutionPolicy.READ_ONLY,

        // 命令工具
        "bash", ToolExecutionPolicy.COMMAND,
        "api_call", ToolExecutionPolicy.COMMAND,

        // 文件修改工具
        "file_writer", ToolExecutionPolicy.FILE_MODIFY,
        "file_delete", ToolExecutionPolicy.FILE_MODIFY
    );

    /**
     * 获取工具执行策略
     */
    public ToolExecutionPolicy getPolicy(String toolName) {
        return toolPolicies.getOrDefault(toolName, ToolExecutionPolicy.COMMAND);
    }
}
```

---

## 17. 权限系统增强设计

### 17.1 三级权限模型（Deny → Ask → Allow）

```java
/**
 * 权限决策结果
 */
public enum PermissionDecision {
    DENY,       // 拒绝执行
    ASK,        // 询问用户
    ALLOW       // 允许执行
}

/**
 * 权限规则
 */
@Data
@Builder
public class PermissionRule {
    private String id;                          // 规则ID
    private String pattern;                     // 匹配模式（支持通配符）
    private PermissionDecision decision;        // 决策结果
    private int priority;                       // 优先级（越高越优先）
    private String description;                 // 规则描述
    private LocalDateTime createdAt;            // 创建时间
}

/**
 * 权限评估服务
 */
@Service
public class PermissionEvaluator {

    private final List<PermissionRule> rules;

    /**
     * 评估权限
     */
    public PermissionDecision evaluate(String userId, String resource, String action) {
        // 按优先级排序的规则列表
        List<PermissionRule> matchedRules = rules.stream()
            .filter(rule -> matches(rule.getPattern(), resource, action))
            .sorted(Comparator.comparingInt(PermissionRule::getPriority).reversed())
            .collect(Collectors.toList());

        // 返回最高优先级的决策
        if (matchedRules.isEmpty()) {
            return PermissionDecision.ASK;  // 默认询问
        }

        return matchedRules.get(0).getDecision();
    }

    /**
     * 批量评估权限
     */
    public Map<String, PermissionDecision> evaluateBatch(
        String userId,
        List<ToolCall> toolCalls
    ) {
        return toolCalls.stream()
            .collect(Collectors.toMap(
                ToolCall::getId,
                call -> evaluate(userId, call.getTool(), "execute")
            ));
    }
}
```

### 17.2 权限配置

```yaml
ai:
  security:
    permission:
      # 拒绝规则（最高优先级）
      deny:
        - pattern: "file:/etc/**"
          description: "禁止访问系统配置目录"
        - pattern: "file:**/.env"
          description: "禁止访问环境变量文件"
        - pattern: "tool:bash:rm -rf /"
          description: "禁止危险命令"

      # 询问规则（中等优先级）
      ask:
        - pattern: "file:src/**/*.java"
          description: "修改源代码需要确认"
        - pattern: "tool:database_query"
          description: "数据库查询需要确认"

      # 允许规则（最低优先级）
      allow:
        - pattern: "file:docs/**"
          description: "允许操作文档目录"
        - pattern: "tool:file_reader"
          description: "允许读取文件"
```

### 17.3 工具权限注解

```java
/**
 * 工具权限注解
 * 用于声明工具的权限要求
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolPermission {
    /**
     * 工具名称
     */
    String value();

    /**
     * 默认权限决策
     */
    PermissionDecision defaultDecision() default PermissionDecision.ASK;

    /**
     * 风险等级
     */
    RiskLevel riskLevel() default RiskLevel.MEDIUM;

    /**
     * 描述
     */
    String description() default "";
}
```

---

## 18. 构建器-验证者代理模式

### 18.1 模式概述

```
┌─────────────────────────────────────────────────────────────────┐
│                    构建器-验证者代理模式                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  用户请求 ──► Builder Agent ──► 产出物 ──► Validator Agent      │
│                  │                           │                   │
│                  ▼                           ▼                   │
│             生成代码/文档              验证质量/规范             │
│                                           │                     │
│                                           ▼                     │
│                                    ┌─────┴─────┐               │
│                                    │           │               │
│                                 通过        不通过              │
│                                    │           │               │
│                                    ▼           ▼               │
│                                 交付      反馈给Builder重做     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 18.2 构建器代理

```java
/**
 * 构建器代理接口
 * 负责生成产出物
 */
public interface BuilderAgent extends Agent {
    /**
     * 构建产出物
     * @param request 构建请求
     * @return 构建结果
     */
    BuildResult build(BuildRequest request);

    /**
     * 根据反馈修复
     * @param feedback 验证反馈
     * @return 修复后的结果
     */
    BuildResult fix(ValidationFeedback feedback);
}

/**
 * 代码构建器代理
 */
@Service
public class CodeBuilderAgent extends BaseAgent implements BuilderAgent {

    @Override
    public BuildResult build(BuildRequest request) {
        // 1. 分析需求
        RequirementAnalysis analysis = analyzeRequirement(request);

        // 2. 生成代码
        String code = generateCode(analysis);

        // 3. 生成测试
        String testCode = generateTest(code, analysis);

        return BuildResult.builder()
            .code(code)
            .testCode(testCode)
            .analysis(analysis)
            .build();
    }

    @Override
    public BuildResult fix(ValidationFeedback feedback) {
        // 根据反馈修复问题
        List<String> issues = feedback.getIssues();
        String fixedCode = applyFixes(feedback.getOriginalCode(), issues);

        return BuildResult.builder()
            .code(fixedCode)
            .fixDescription(generateFixDescription(issues))
            .build();
    }
}
```

### 18.3 验证者代理

```java
/**
 * 验证者代理接口
 * 负责验证产出物质量
 */
public interface ValidatorAgent extends Agent {
    /**
     * 验证产出物
     * @param buildResult 构建结果
     * @return 验证结果
     */
    ValidationResult validate(BuildResult buildResult);
}

/**
 * 代码验证器代理
 */
@Service
public class CodeValidatorAgent extends BaseAgent implements ValidatorAgent {

    @Override
    public ValidationResult validate(BuildResult buildResult) {
        List<ValidationIssue> issues = new ArrayList<>();

        // 1. 代码规范检查
        issues.addAll(checkCodeStyle(buildResult.getCode()));

        // 2. 安全检查
        issues.addAll(checkSecurity(buildResult.getCode()));

        // 3. 逻辑检查
        issues.addAll(checkLogic(buildResult.getCode()));

        // 4. 测试覆盖检查
        issues.addAll(checkTestCoverage(buildResult));

        return ValidationResult.builder()
            .passed(issues.isEmpty())
            .issues(issues)
            .score(calculateScore(issues))
            .build();
    }
}
```

### 18.4 协作流程

```java
/**
 * 构建验证协调器
 */
@Service
public class BuildValidateCoordinator {

    private final BuilderAgent builder;
    private final ValidatorAgent validator;
    private static final int MAX_ITERATIONS = 3;

    /**
     * 执行构建-验证流程
     */
    public BuildResult execute(BuildRequest request) {
        BuildResult result = builder.build(request);
        int iteration = 0;

        while (iteration < MAX_ITERATIONS) {
            ValidationResult validation = validator.validate(result);

            if (validation.isPassed()) {
                return result;
            }

            // 验证不通过，修复问题
            ValidationFeedback feedback = ValidationFeedback.builder()
                .originalCode(result.getCode())
                .issues(validation.getIssues())
                .build();

            result = builder.fix(feedback);
            iteration++;
        }

        // 超过最大迭代次数，返回最后结果并标记
        result.setNeedsManualReview(true);
        return result;
    }
}
```

---

## 19. 版本记录

| 版本   | 日期         | 作者     | 描述                  |
|------|------------|--------|---------------------|
| v1.0 | 2026-03-21 | Claude | 初稿：AI 对话功能          |
| v1.1 | 2026-03-21 | Claude | 补充状态设计、错误处理         |
| v2.0 | 2026-03-21 | Claude | 升级为 AI Agent 智能体框架  |
| v2.1 | 2026-03-21 | Claude | 模块化重构：抽象工厂 + 策略模式   |
| v2.2 | 2026-03-21 | Claude | 补充 Claude Code 最佳实践 |

---

## 20. 待确认事项

### 已确认

- [x] **Agent 类型**：DevAgent（开发助手）
- [x] **工具范围**：project_reader、file_reader、file_writer、code_search
- [x] **记忆系统**：初期实现向量记忆
- [x] **向量数据库**：MySQL + JSON（初期简单方案）
- [x] **Embedding 模型**：智谱 Embedding-3
- [x] **智谱 API Key**：已准备好
- [x] **文件写入权限**：不限制目录
- [x] **最大并发**：不超过 3

### 待确认

（无）
