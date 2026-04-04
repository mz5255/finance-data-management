# AI Agent 智能体技术设计文档

## 1. 技术栈

### 1.1 后端技术栈

| 技术          | 版本    | 用途      |
|-------------|-------|---------|
| Java        | 21    | 编程语言    |
| Spring Boot | 3.2.2 | 应用框架    |
| Spring MVC  | 6.1.x | Web 框架  |
| MyBatis     | 3.0.x | ORM 框架  |
| MySQL       | 8.0+  | 关系数据库   |
| Redis       | 7.0+  | 缓存/会话存储 |
| SpringDoc   | 2.3+  | API 文档  |
| WebSocket   | -     | 实时通信    |
| WebFlux     | -     | 响应式流    |

### 1.2 前端技术栈

| 技术           | 版本     | 用途          |
|--------------|--------|-------------|
| Vue          | 3.5.x  | 前端框架        |
| Element Plus | 2.13.x | UI 组件库      |
| Axios        | 1.6.x  | HTTP 客户端    |
| Pinia        | 2.x    | 状态管理        |
| Vue Router   | 4.x    | 路由          |
| Vite         | 7.x    | 构建工具        |
| Markdown-it  | -      | Markdown 渲染 |
| Highlight.js | -      | 代码高亮        |

### 1.3 AI 模型

| 模型          | 用途         | 提供商   |
|-------------|------------|-------|
| GLM-5       | 主模型（对话、推理） | 智谱 AI |
| Embedding-3 | 向量嵌入       | 智谱 AI |

---

## 2. 系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              用户交互层                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                         │
│  │  Web 对话   │  │  API 接口   │  │  WebSocket  │                         │
│  └─────────────┘  └─────────────┘  └─────────────┘                         │
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
│                            Agent 核心层（模块化）                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │AgentModule  │  │ ToolModule  │  │MemoryModule │  │ ModelModule │        │
│  │   工厂+策略  │  │   工厂+策略  │  │   工厂+策略  │  │   工厂+策略  │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            基础设施层                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  配置管理   │  │  日志监控   │  │  安全控制   │  │  持久化     │        │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 模块依赖关系

```
module-ai/
├── depends on → finance-common (工具类)
├── depends on → finance-datasource (数据访问)
└── provides → AI Agent 能力
```

---

## 3. 数据库设计

### 3.1 ER 图

```
┌──────────────────┐     ┌──────────────────┐
│  ai_conversation │     │   ai_message     │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │────<│ id (PK)          │
│ user_id          │     │ conversation_id  │
│ title            │     │ role             │
│ status           │     │ content          │
│ created_at       │     │ token_count      │
│ updated_at       │     │ created_at       │
└──────────────────┘     └──────────────────┘

┌──────────────────┐     ┌──────────────────┐
│   ai_memory      │     │ ai_memory_vector │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │────<│ id (PK)          │
│ user_id          │     │ memory_id        │
│ conversation_id  │     │ embedding (JSON) │
│ type             │     │ created_at       │
│ content          │     └──────────────────┘
│ importance       │
│ created_at       │
└──────────────────┘

┌──────────────────┐     ┌──────────────────┐
│  ai_task_plan    │     │  ai_file_change  │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │     │ id (PK)          │
│ plan_id          │────<│ change_id        │
│ user_id          │     │ plan_id          │
│ goal             │     │ file_path        │
│ sub_tasks (JSON) │     │ change_type      │
│ current_step     │     │ original_content │
│ status           │     │ new_content      │
│ created_at       │     │ created_at       │
└──────────────────┘     └──────────────────┘

┌──────────────────┐     ┌──────────────────┐
│ai_session_state  │     │  ai_token_usage  │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │     │ id (PK)          │
│ conversation_id  │     │ user_id          │
│ user_id          │     │ conversation_id  │
│ context (JSON)   │     │ model_id         │
│ status           │     │ input_tokens     │
│ created_at       │     │ output_tokens    │
│ last_active_at   │     │ cost             │
└──────────────────┘     │ created_at       │
                         └──────────────────┘
```

### 3.2 表结构详情

#### 3.2.1 会话表 (ai_conversation)

```sql
CREATE TABLE ai_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    title VARCHAR(256) COMMENT '会话标题',
    agent_type VARCHAR(32) DEFAULT 'chat' COMMENT 'Agent类型:chat/code/data',
    model_id VARCHAR(32) DEFAULT 'zhipu' COMMENT '使用的模型ID',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态:ACTIVE/ARCHIVED/DELETED',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    total_tokens BIGINT DEFAULT 0 COMMENT '总Token消耗',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表';
```

#### 3.2.2 消息表 (ai_message)

```sql
CREATE TABLE ai_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    message_id VARCHAR(64) NOT NULL COMMENT '消息ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    role VARCHAR(32) NOT NULL COMMENT '角色:USER/ASSISTANT/SYSTEM/TOOL',
    content TEXT NOT NULL COMMENT '消息内容',
    token_count INT DEFAULT 0 COMMENT 'Token数量',
    tool_calls JSON COMMENT '工具调用信息',
    tool_result JSON COMMENT '工具执行结果',
    metadata JSON COMMENT '元数据',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_message_id (message_id),
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';
```

#### 3.2.3 记忆表 (ai_memory)

```sql
CREATE TABLE ai_memory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    memory_id VARCHAR(64) NOT NULL COMMENT '记忆ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) COMMENT '关联会话ID',
    type VARCHAR(32) NOT NULL COMMENT '类型:SHORT_TERM/LONG_TERM/EPISODE',
    content TEXT NOT NULL COMMENT '记忆内容',
    importance INT DEFAULT 5 COMMENT '重要性:1-10',
    access_count INT DEFAULT 0 COMMENT '访问次数',
    last_access_at DATETIME COMMENT '最后访问时间',
    expires_at DATETIME COMMENT '过期时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_memory_id (memory_id),
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_importance (importance)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI记忆表';
```

#### 3.2.4 记忆向量表 (ai_memory_vector)

```sql
CREATE TABLE ai_memory_vector (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    memory_id VARCHAR(64) NOT NULL COMMENT '记忆ID',
    embedding JSON NOT NULL COMMENT '向量嵌入(JSON数组)',
    model_id VARCHAR(32) DEFAULT 'zhipu-embedding-3' COMMENT '嵌入模型ID',
    dimension INT DEFAULT 1024 COMMENT '向量维度',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_memory_id (memory_id),
    INDEX idx_model_id (model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI记忆向量表';
```

#### 3.2.5 任务计划表 (ai_task_plan)

```sql
CREATE TABLE ai_task_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    plan_id VARCHAR(64) NOT NULL COMMENT '计划ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) COMMENT '关联会话ID',
    goal TEXT NOT NULL COMMENT '总目标',
    sub_tasks JSON NOT NULL COMMENT '子任务列表',
    dependencies JSON COMMENT '依赖关系',
    current_step INT DEFAULT 0 COMMENT '当前步骤',
    total_steps INT DEFAULT 0 COMMENT '总步骤数',
    status VARCHAR(32) DEFAULT 'PENDING' COMMENT '状态:PENDING/RUNNING/COMPLETED/ERROR',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_plan_id (plan_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI任务计划表';
```

#### 3.2.6 文件变更记录表 (ai_file_change)

```sql
CREATE TABLE ai_file_change (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    change_id VARCHAR(64) NOT NULL COMMENT '变更ID',
    plan_id VARCHAR(64) NOT NULL COMMENT '所属计划ID',
    file_path VARCHAR(512) NOT NULL COMMENT '文件路径',
    change_type VARCHAR(32) NOT NULL COMMENT '变更类型:CREATE/MODIFY/DELETE',
    original_content LONGTEXT COMMENT '原始内容',
    new_content LONGTEXT COMMENT '新内容',
    checksum VARCHAR(64) COMMENT '内容校验和',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_plan_id (plan_id),
    INDEX idx_change_id (change_id),
    INDEX idx_file_path (file_path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI文件变更记录表';
```

#### 3.2.7 会话状态表 (ai_session_state)

```sql
CREATE TABLE ai_session_state (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    context JSON COMMENT '会话上下文',
    current_plan_id VARCHAR(64) COMMENT '当前执行计划ID',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态:ACTIVE/COMPLETED/EXPIRED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_active_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    UNIQUE KEY uk_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_last_active (last_active_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话状态表';
```

#### 3.2.8 Token 使用记录表 (ai_token_usage)

```sql
CREATE TABLE ai_token_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) COMMENT '会话ID',
    model_id VARCHAR(32) NOT NULL COMMENT '模型ID',
    request_type VARCHAR(32) NOT NULL COMMENT '请求类型:CHAT/EMBEDDING',
    input_tokens INT DEFAULT 0 COMMENT '输入Token数',
    output_tokens INT DEFAULT 0 COMMENT '输出Token数',
    total_tokens INT DEFAULT 0 COMMENT '总Token数',
    cost DECIMAL(10,6) DEFAULT 0 COMMENT '成本(元)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_model_id (model_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Token使用记录表';
```

---

## 4. 模块设计

### 4.1 模块结构

```
module-ai/
└── src/main/java/cn/com/mz/app/finance/ai/
    ├── module/                    # 模块核心
    │   ├── AiModule.java          # 模块接口
    │   └── ModuleRegistry.java    # 模块注册中心
    │
    ├── agent/                     # Agent 模块
    │   ├── AgentModule.java
    │   ├── AgentFactory.java
    │   ├── strategy/              # 策略模式
    │   │   ├── AgentStrategy.java
    │   │   ├── ChatAgentStrategy.java
    │   │   └── CodeAgentStrategy.java
    │   └── Agent.java
    │
    ├── tool/                      # Tool 模块
    │   ├── ToolModule.java
    │   ├── ToolFactory.java
    │   ├── strategy/
    │   │   ├── ToolStrategy.java
    │   │   ├── FileToolStrategy.java
    │   │   └── DbToolStrategy.java
    │   └── ToolExecutor.java
    │
    ├── memory/                    # Memory 模块
    │   ├── MemoryModule.java
    │   ├── MemoryFactory.java
    │   ├── strategy/
    │   │   ├── MemoryStrategy.java
    │   │   ├── ShortTermStrategy.java
    │   │   └── VectorStrategy.java
    │   └── compaction/
    │       └── CompactionStrategy.java
    │
    ├── model/                     # Model 模块
    │   ├── ModelModule.java
    │   ├── ModelFactory.java
    │   └── strategy/
    │       ├── ModelStrategy.java
    │       ├── ZhipuStrategy.java
    │       └── ClaudeStrategy.java
    │
    ├── orchestration/             # 编排模块
    │   ├── TaskDecompositionStrategy.java
    │   ├── ProgressTrackingStrategy.java
    │   └── RollbackStrategy.java
    │
    ├── security/                  # 安全模块
    │   ├── HumanLoopStrategy.java
    │   └── PermissionStrategy.java
    │
    ├── controller/                # API 控制器
    │   ├── ChatController.java
    │   └── AgentController.java
    │
    ├── service/                   # 业务服务
    │   ├── AgentOrchestrator.java
    │   └── ConversationService.java
    │
    ├── config/                    # 配置
    │   ├── AiAutoConfiguration.java
    │   └── AiProperties.java
    │
    └── exception/                 # 异常
        └── AgentException.java
```

### 4.2 核心类设计

#### 4.2.1 模块接口

```java
/**
 * AI 模块接口
 * 所有功能模块都需要实现此接口
 */
public interface AiModule {
    /**
     * 获取模块名称
     * @return 模块名称
     */
    String getName();

    /**
     * 模块初始化
     */
    void initialize();

    /**
     * 获取模块工厂
     * @return 工厂实例
     */
    AiModuleFactory<?> getFactory();
}
```

#### 4.2.2 策略接口

```java
/**
 * Agent 策略接口
 * 定义不同类型 Agent 的行为
 */
public interface AgentStrategy {
    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getName();

    /**
     * 创建 Agent 实例
     * @param config 配置信息
     * @return Agent 实例
     */
    Agent createAgent(AiModuleConfig config);

    /**
     * 获取系统提示词
     * @return 提示词内容
     */
    String getSystemPrompt();

    /**
     * 获取可用工具列表
     * @return 工具名称列表
     */
    List<String> getAvailableTools();
}
```

### 4.3 类图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              <<interface>>                                   │
│                               AiModule                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getName(): String                                                          │
│ + initialize(): void                                                         │
│ + getFactory(): AiModuleFactory<?>                                           │
└─────────────────────────────────────────────────────────────────────────────┘
                                    △
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│ AgentModule   │         │ ToolModule    │         │MemoryModule   │
├───────────────┤         ├───────────────┤         ├───────────────┤
│ - factory     │         │ - factory     │         │ - factory     │
├───────────────┤         ├───────────────┤         ├───────────────┤
│ +getName()    │         │ +getName()    │         │ +getName()    │
│ +initialize() │         │ +initialize() │         │ +initialize() │
│ +getFactory() │         │ +getFactory() │         │ +getFactory() │
└───────────────┘         └───────────────┘         └───────────────┘
```

---

## 5. 配置说明

### 5.1 完整配置文件

```yaml
# ============================================================
# AI Agent 智能体框架配置文件
# 文件路径: application-ai.yml
# ============================================================

ai:
  # ============================================================
  # 模型配置 - 定义可用的 LLM 模型
  # ============================================================
  models:
    # 智谱 GLM 模型（默认）
    zhipu:
      enabled: true                                    # 是否启用该模型
      api-key: ${ZHIPU_API_KEY:}                       # API密钥，从环境变量读取
      model: glm-5                                     # 模型名称
      base-url: https://open.bigmodel.cn/api/paas/v4   # API地址
      max-tokens: 4096                                 # 最大Token数
      temperature: 0.7                                 # 温度参数(0-1)
      timeout: 30000                                   # 超时时间(毫秒)
      retry-times: 3                                   # 重试次数

    # Claude 模型（可选）
    claude:
      enabled: false                                   # 是否启用
      api-key: ${ANTHROPIC_API_KEY:}                   # API密钥
      model: claude-sonnet-4-6                         # 模型名称
      base-url: https://api.anthropic.com              # API地址
      max-tokens: 8192                                 # 最大Token数

  # ============================================================
  # 默认配置
  # ============================================================
  default-model: zhipu                                # 默认模型
  default-agent: chat                                 # 默认Agent类型

  # ============================================================
  # Agent 配置
  # ============================================================
  agents:
    dev:
      model: zhipu                                     # 使用的模型
      tools: [project_reader, file_reader, file_writer, code_search]
      max-context-rounds: 20                          # 最大上下文轮数
      system-prompt: |
        你是一个全栈开发助手，完全了解当前项目的架构和代码。
        你的职责是理解用户需求，生成符合规范的技术方案和代码。

  # ============================================================
  # 工具配置
  # ============================================================
  tools:
    project_reader:
      enabled: true                                    # 是否启用
      project-root: ${PROJECT_ROOT:./}                 # 项目根目录

    file_reader:
      enabled: true
      allowed-extensions: [.java, .xml, .yml, .properties, .vue, .js, .ts, .md]
      max-file-size: 1MB                               # 最大文件大小

    file_writer:
      enabled: true
      require-confirmation: false                      # 是否需要确认
      allowed-directories: []                          # 允许的目录(空=不限制)

  # ============================================================
  # 记忆配置
  # ============================================================
  memory:
    short-term:
      max-messages: 20                                 # 最大消息数
      expire-minutes: 60                              # 过期时间(分钟)

    vector:
      enabled: true                                    # 是否启用向量记忆
      embedding-model: zhipu-embedding-3              # 嵌入模型
      similarity-threshold: 0.7                        # 相似度阈值
      top-k: 5                                         # 返回数量

    compaction:
      enabled: true                                    # 是否启用压缩
      strategy: summary                                # 压缩策略
      max-tokens: 4000                                 # 触发阈值
      target-ratio: 0.3                                # 目标比例

  # ============================================================
  # 安全配置
  # ============================================================
  security:
    prompt-injection-check: true                       # Prompt注入检测
    sensitive-data-check: true                         # 敏感数据检测
    sensitive-patterns: [phone, id_card, bank_card, email, password, api_key]

    rate-limit:
      enabled: true                                    # 是否启用限流
      requests-per-minute: 20                          # 每分钟请求数
      tokens-per-day: 100000                           # 每日Token数

  # ============================================================
  # 并发配置
  # ============================================================
  concurrency:
    max-requests: 3                                    # 最大并发数
    queue-size: 10                                     # 队列大小
    timeout-seconds: 300                              # 超时时间(秒)
```

### 5.2 环境变量

```bash
# 必需的环境变量
export ZHIPU_API_KEY="your-zhipu-api-key"

# 可选的环境变量
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export PROJECT_ROOT="/path/to/project"
```

---

## 6. 部署说明

### 6.1 开发环境

```bash
# 1. 克隆项目
git clone <repository-url>
cd finance-data-management

# 2. 配置环境变量
export ZHIPU_API_KEY="your-api-key"

# 3. 启动后端
cd finance-application
mvn spring-boot:run

# 4. 启动前端
cd finance-data-management-view
npm install
npm run dev
```

### 6.2 生产环境

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "58888:58888"
    environment:
      - ZHIPU_API_KEY=${ZHIPU_API_KEY}
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_DATABASE: finance
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7.0
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

---

## 7. 监控与日志

### 7.1 日志配置

```yaml
# logback-spring.xml
logging:
  level:
    cn.com.mz.app.finance.ai: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/ai-agent.log
    max-size: 10MB
    max-history: 30
```

### 7.2 健康检查

```bash
# 健康检查端点
GET /actuator/health

# 响应示例
{
  "status": "UP",
  "components": {
    "ai": {"status": "UP"},
    "db": {"status": "UP"},
    "redis": {"status": "UP"}
  }
}
```

---

## 8. 版本记录

| 版本   | 日期         | 作者     | 描述 |
|------|------------|--------|----|
| v1.0 | 2026-03-21 | Claude | 初稿 |
