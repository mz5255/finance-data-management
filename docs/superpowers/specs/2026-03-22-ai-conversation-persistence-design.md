# AI 对话历史持久化设计

## 背景

当前 AI 对话功能存在以下问题：
1. 后端使用内存存储，服务重启后数据丢失
2. 前端解析后端返回数据格式不正确，导致看不到历史对话
3. 会话未关联用户，所有人共享同一份会话列表
4. 字段名不匹配（`conversationId` vs `id`，`lastMessageAt` vs `updateTime`）

## 目标

- 实现会话和消息的数据库持久化
- 会话关联用户，每个用户只能看到自己的会话
- 修复前端数据解析问题
- 支持会话标题可编辑

## 技术方案

### 一、数据库设计

#### 会话表 `ai_conversations`

| 字段 | 类型 | 必填 | 说明 |
|-----|------|-----|-----|
| id | BIGINT | Y | 主键（自增） |
| conversation_id | VARCHAR(32) | Y | 会话唯一标识（业务ID） |
| user_id | BIGINT | Y | 关联用户 ID |
| title | VARCHAR(100) | N | 会话标题 |
| agent_type | VARCHAR(20) | Y | Agent 类型（chat/code/data） |
| model_id | VARCHAR(50) | N | 模型 ID |
| message_count | INT | Y | 消息数量，默认 0 |
| status | VARCHAR(20) | Y | 状态（ACTIVE/DELETED），默认 ACTIVE |
| deleted | INT | Y | 软删除标记，默认 0 |
| lock_version | INT | Y | 乐观锁版本号，默认 0 |
| create_time | DATETIME | Y | 创建时间 |
| update_time | DATETIME | Y | 更新时间 |

索引：
- `uk_conversation_id` UNIQUE (conversation_id)
- `idx_user_id` (user_id)
- `idx_user_agent_status` (user_id, agent_type, status)

#### 消息表 `ai_messages`

| 字段 | 类型 | 必填 | 说明 |
|-----|------|-----|-----|
| id | BIGINT | Y | 主键（自增） |
| message_id | VARCHAR(32) | Y | 消息唯一标识（业务ID） |
| conversation_id | VARCHAR(32) | Y | 关联会话 ID |
| role | VARCHAR(20) | Y | 角色（user/assistant/system） |
| content | TEXT | N | 消息内容 |
| tool_calls | JSON | N | 工具调用信息 |
| deleted | INT | Y | 软删除标记，默认 0 |
| lock_version | INT | Y | 乐观锁版本号，默认 0 |
| create_time | DATETIME | Y | 创建时间 |
| update_time | DATETIME | Y | 更新时间 |

索引：
- `uk_message_id` UNIQUE (message_id)
- `idx_conversation_id` (conversation_id)

### 二、后端实现

#### 2.1 实体类

**AiConversationDO** (`finance-datasource` 模块)
- 继承 `BaseEntity`
- 使用 `@TableName("ai_conversations")`
- 字段：conversationId, userId, title, agentType, modelId, messageCount, status

**AiMessageDO** (`finance-datasource` 模块)
- 继承 `BaseEntity`
- 使用 `@TableName("ai_messages")`
- 字段：messageId, conversationId, role, content, toolCalls (使用 TypeHandler 处理 JSON)

#### 2.2 Mapper

**AiConversationMapper** - 继承 `BaseMapper<AiConversationDO>`
**AiMessageMapper** - 继承 `BaseMapper<AiMessageDO>`

#### 2.3 Service 修改

修改 `ConversationServiceImpl`：
- 注入 `AiConversationMapper` 和 `AiMessageMapper`
- 从 `StpUtil.getLoginIdAsLong()` 获取当前用户 ID
- 所有查询添加用户过滤条件
- 使用数据库操作替换内存操作

#### 2.4 新增 API

**PUT /api/finance-data/ai/conversations/{conversationId}**
- 请求体：`{ "title": "新标题" }`
- 用于更新会话标题

### 三、前端修改

#### 3.1 修复数据解析 (`index.vue`)

```javascript
// loadConversations 方法
const res = await aiApi.getConversations({ page: 1, size: 50 })
const rawData = res.data?.conversations || res?.conversations || []
allConversations.value = rawData.map(conv => ({
  ...conv,
  id: conv.conversationId,
  updateTime: conv.lastMessageAt
}))
```

#### 3.2 修复消息历史解析 (`selectConversation` 方法)

```javascript
const res = await aiApi.getConversationMessages(conv.id)
const rawData = res.data?.messages || res?.messages || []
conversationMessages[conv.id] = rawData.map(msg => ({
  ...msg,
  id: msg.messageId
}))
```

#### 3.3 新增编辑标题功能

- 双击会话标题可编辑
- 调用 `aiApi.updateConversation(id, { title })` 更新

## 文件清单

### 后端新增
- `finance-datasource/.../entity/ai/AiConversationDO.java`
- `finance-datasource/.../entity/ai/AiMessageDO.java`
- `finance-datasource/.../mapper/ai/AiConversationMapper.java`
- `finance-datasource/.../mapper/ai/AiMessageMapper.java`

### 后端修改
- `module-ai-agent/.../service/impl/ConversationServiceImpl.java`
- `module-ai-agent/.../dto/request/ConversationUpdateRequest.java` (新增)
- `module-ai-agent/.../webapi/ai/ConversationController.java`

### 前端修改
- `src/views/ai/index.vue`
- `src/api/ai.js` (添加 updateConversation 调用)

## 注意事项

1. 用户未登录时，需要处理获取用户 ID 的异常
2. 消息内容的 `toolCalls` 字段使用 JSON 存储，需要配置 TypeHandler
3. 前端字段映射保持一致性，所有地方都使用 `conversationId` 和 `lastMessageAt`
