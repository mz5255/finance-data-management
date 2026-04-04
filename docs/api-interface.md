# AI Agent API 接口文档

## 1. 接口概述

### 1.1 基础信息

| 项目   | 说明                      |
|------|-------------------------|
| 基础路径 | `/api/ai`               |
| 认证方式 | Bearer Token (SA-Token) |
| 内容类型 | `application/json`      |
| 字符编码 | UTF-8                   |

### 1.2 通用响应格式

```json
{
  "code": 200,                    // 状态码: 200成功, 4xx客户端错误, 5xx服务端错误
  "message": "success",           // 响应消息
  "data": {},                     // 响应数据
  "timestamp": 1711020800000      // 时间戳(毫秒)
}
```

### 1.3 错误码定义

| 错误码  | 说明          |
|------|-------------|
| 200  | 成功          |
| 400  | 请求参数错误      |
| 401  | 未授权         |
| 403  | 权限不足        |
| 404  | 资源不存在       |
| 429  | 请求频率超限      |
| 500  | 服务器内部错误     |
| 503  | 服务暂时不可用     |
| 1001 | 模型调用失败      |
| 1002 | Token 超限    |
| 1003 | 上下文溢出       |
| 1004 | 工具执行失败      |
| 1005 | Prompt 注入检测 |

---

## 2. 对话接口

### 2.1 发送消息

**接口**: `POST /api/ai/chat`

**描述**: 发送消息给 AI Agent，支持流式和非流式响应

**请求参数**:

| 参数名            | 类型      | 必填 | 说明                          |
|----------------|---------|----|-----------------------------|
| message        | String  | 是  | 用户消息内容                      |
| conversationId | String  | 否  | 会话ID，不传则创建新会话               |
| agentType      | String  | 否  | Agent类型: chat(默认)/code/data |
| modelId        | String  | 否  | 指定模型ID，不传使用默认               |
| stream         | Boolean | 否  | 是否流式输出，默认true               |

**请求示例**:

```json
{
  "message": "帮我分析上个月的财务数据",
  "conversationId": "conv-123",
  "agentType": "data",
  "stream": true
}
```

**响应示例（非流式）**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": "conv-123",
    "messageId": "msg-456",
    "content": "根据查询结果，上个月的财务数据如下...",
    "toolCalls": [
      {
        "toolId": "database_query",
        "toolName": "数据库查询",
        "status": "SUCCESS",
        "result": "查询到15条记录"
      }
    ],
    "tokenUsage": {
      "inputTokens": 256,
      "outputTokens": 512,
      "totalTokens": 768
    }
  }
}
```

**响应示例（流式 SSE）**:

```
event: thought
data: {"content": "我需要先查询数据库获取财务数据..."}

event: tool_call
data: {"tool": "database_query", "status": "executing", "callId": "call-001"}

event: tool_result
data: {"tool": "database_query", "status": "success", "result": "查询到15条记录"}

event: message
data: {"content": "根据查询结果，上个月的财务数据如下..."}

event: done
data: {"conversationId": "conv-123", "messageId": "msg-456"}
```

---

### 2.2 获取会话历史

**接口**: `GET /api/ai/conversations/{conversationId}/messages`

**描述**: 获取指定会话的消息历史

**路径参数**:

| 参数名            | 类型     | 必填 | 说明   |
|----------------|--------|----|------|
| conversationId | String | 是  | 会话ID |

**查询参数**:

| 参数名   | 类型      | 必填 | 说明                 |
|-------|---------|----|--------------------|
| page  | Integer | 否  | 页码，默认1             |
| size  | Integer | 否  | 每页数量，默认20          |
| order | String  | 否  | 排序: asc/desc，默认asc |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": "conv-123",
    "title": "财务数据分析",
    "messages": [
      {
        "messageId": "msg-001",
        "role": "USER",
        "content": "帮我分析上个月的财务数据",
        "createdAt": "2026-03-21T10:00:00"
      },
      {
        "messageId": "msg-002",
        "role": "ASSISTANT",
        "content": "根据查询结果...",
        "toolCalls": [...],
        "createdAt": "2026-03-21T10:00:05"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 20
  }
}
```

---

### 2.3 创建新会话

**接口**: `POST /api/ai/conversations`

**描述**: 创建新的对话会话

**请求参数**:

| 参数名       | 类型     | 必填 | 说明             |
|-----------|--------|----|----------------|
| title     | String | 否  | 会话标题           |
| agentType | String | 否  | Agent类型，默认chat |
| modelId   | String | 否  | 指定模型ID         |

**请求示例**:

```json
{
  "title": "代码生成任务",
  "agentType": "code",
  "modelId": "zhipu"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": "conv-new-123",
    "title": "代码生成任务",
    "agentType": "code",
    "modelId": "zhipu",
    "createdAt": "2026-03-21T10:00:00"
  }
}
```

---

### 2.4 获取会话列表

**接口**: `GET /api/ai/conversations`

**描述**: 获取当前用户的会话列表

**查询参数**:

| 参数名       | 类型      | 必填 | 说明                          |
|-----------|---------|----|-----------------------------|
| status    | String  | 否  | 状态: ACTIVE/ARCHIVED/DELETED |
| agentType | String  | 否  | Agent类型过滤                   |
| page      | Integer | 否  | 页码，默认1                      |
| size      | Integer | 否  | 每页数量，默认20                   |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversations": [
      {
        "conversationId": "conv-123",
        "title": "财务数据分析",
        "agentType": "data",
        "messageCount": 10,
        "lastMessageAt": "2026-03-21T10:30:00",
        "status": "ACTIVE"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  }
}
```

---

### 2.5 删除会话

**接口**: `DELETE /api/ai/conversations/{conversationId}`

**描述**: 删除指定会话（软删除）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 3. Agent 管理接口

### 3.1 获取可用 Agent 列表

**接口**: `GET /api/ai/agents`

**描述**: 获取系统中所有可用的 Agent 类型

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "agents": [
      {
        "type": "chat",
        "name": "通用助手",
        "description": "通用对话Agent，适合日常问答",
        "tools": ["search", "calculator"],
        "icon": "chat-icon"
      },
      {
        "type": "code",
        "name": "开发助手",
        "description": "代码开发和项目分析Agent",
        "tools": ["project_reader", "file_reader", "file_writer", "code_search"],
        "icon": "code-icon"
      },
      {
        "type": "data",
        "name": "数据分析",
        "description": "财务数据分析和报表生成",
        "tools": ["database_query", "calculator", "chart_generator"],
        "icon": "data-icon"
      }
    ]
  }
}
```

---

### 3.2 获取 Agent 详情

**接口**: `GET /api/ai/agents/{agentType}`

**描述**: 获取指定类型 Agent 的详细信息

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "type": "code",
    "name": "开发助手",
    "description": "代码开发和项目分析Agent",
    "systemPrompt": "你是一个全栈开发助手...",
    "tools": [
      {
        "name": "project_reader",
        "description": "读取项目结构",
        "parameters": {...}
      },
      {
        "name": "file_reader",
        "description": "读取文件内容",
        "parameters": {...}
      }
    ],
    "config": {
      "maxContextRounds": 20,
      "defaultModel": "zhipu"
    }
  }
}
```

---

## 4. 工具管理接口

### 4.1 获取可用工具列表

**接口**: `GET /api/ai/tools`

**描述**: 获取系统中所有可用的工具

**查询参数**:

| 参数名      | 类型     | 必填 | 说明                             |
|----------|--------|----|--------------------------------|
| category | String | 否  | 工具分类: file/database/api/system |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tools": [
      {
        "name": "project_reader",
        "description": "读取项目结构和文件内容",
        "category": "file",
        "riskLevel": "LOW",
        "requireConfirmation": false,
        "parameters": {
          "type": "object",
          "properties": {
            "action": {"type": "string", "enum": ["structure", "tech_stack", "module_info"]},
            "module": {"type": "string"}
          }
        }
      },
      {
        "name": "file_writer",
        "description": "写入文件内容",
        "category": "file",
        "riskLevel": "HIGH",
        "requireConfirmation": true,
        "parameters": {
          "type": "object",
          "properties": {
            "path": {"type": "string"},
            "content": {"type": "string"},
            "mode": {"type": "string", "enum": ["create", "append", "overwrite"]}
          }
        }
      }
    ]
  }
}
```

---

### 4.2 获取工具详情

**接口**: `GET /api/ai/tools/{toolName}`

**描述**: 获取指定工具的详细信息

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "name": "file_writer",
    "description": "写入文件内容",
    "category": "file",
    "riskLevel": "HIGH",
    "requireConfirmation": true,
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
        "mode": {
          "type": "string",
          "enum": ["create", "append", "overwrite"],
          "default": "create",
          "description": "写入模式"
        }
      },
      "required": ["path", "content"]
    },
    "examples": [
      {
        "description": "创建新文件",
        "parameters": {
          "path": "src/main/java/Example.java",
          "content": "public class Example {}",
          "mode": "create"
        }
      }
    ]
  }
}
```

---

## 5. 任务计划接口

### 5.1 获取任务进度

**接口**: `GET /api/ai/plans/{planId}/progress`

**描述**: 获取任务计划的执行进度

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "planId": "plan-123",
    "goal": "实现用户管理功能",
    "status": "RUNNING",
    "currentStep": 3,
    "totalSteps": 6,
    "percentage": 50,
    "subTasks": [
      {
        "taskId": "task-1",
        "description": "分析现有用户模块",
        "status": "COMPLETED",
        "result": "已识别3个相关文件"
      },
      {
        "taskId": "task-2",
        "description": "生成PRD文档",
        "status": "COMPLETED",
        "result": "docs/prd-user.md"
      },
      {
        "taskId": "task-3",
        "description": "设计API接口",
        "status": "RUNNING",
        "result": null
      },
      {
        "taskId": "task-4",
        "description": "生成后端代码",
        "status": "PENDING",
        "result": null
      },
      {
        "taskId": "task-5",
        "description": "生成前端页面",
        "status": "PENDING",
        "result": null
      },
      {
        "taskId": "task-6",
        "description": "编写测试用例",
        "status": "PENDING",
        "result": null
      }
    ],
    "files": [
      {
        "path": "docs/prd-user.md",
        "type": "CREATE",
        "status": "COMPLETED"
      }
    ]
  }
}
```

---

### 5.2 订阅进度更新（WebSocket）

**接口**: `WS /ws/ai/plans/{planId}/progress`

**描述**: 通过 WebSocket 订阅任务进度实时更新

**连接示例**:

```javascript
const ws = new WebSocket('ws://localhost:58888/ws/ai/plans/plan-123/progress');

ws.onmessage = (event) => {
  const progress = JSON.parse(event.data);
  console.log('Progress:', progress);
};
```

**消息格式**:

```json
{
  "planId": "plan-123",
  "currentStep": 3,
  "totalSteps": 6,
  "percentage": 50,
  "status": "RUNNING",
  "message": "正在设计API接口...",
  "timestamp": "2026-03-21T10:05:30"
}
```

---

### 5.3 回滚任务

**接口**: `POST /api/ai/plans/{planId}/rollback`

**描述**: 回滚任务计划产生的所有文件变更

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "planId": "plan-123",
    "rollbackCount": 3,
    "files": [
      {
        "path": "src/main/java/UserController.java",
        "action": "DELETED",
        "status": "SUCCESS"
      },
      {
        "path": "src/main/java/UserService.java",
        "action": "RESTORED",
        "status": "SUCCESS"
      },
      {
        "path": "docs/prd-user.md",
        "action": "DELETED",
        "status": "SUCCESS"
      }
    ]
  }
}
```

---

## 6. 文件操作接口

### 6.1 预览文件变更

**接口**: `GET /api/ai/plans/{planId}/changes`

**描述**: 预览任务计划产生的文件变更

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "planId": "plan-123",
    "changes": [
      {
        "changeId": "change-1",
        "filePath": "src/main/java/UserController.java",
        "changeType": "CREATE",
        "newContentPreview": "public class UserController {...}",
        "createdAt": "2026-03-21T10:05:00"
      },
      {
        "changeId": "change-2",
        "filePath": "src/main/java/UserService.java",
        "changeType": "MODIFY",
        "diffPreview": "- old line\n+ new line",
        "createdAt": "2026-03-21T10:05:30"
      }
    ]
  }
}
```

---

## 7. 确认接口

### 7.1 请求用户确认

**接口**: `POST /api/ai/confirmations`

**描述**: AI 请求用户确认某个操作（内部接口）

**请求参数**:

| 参数名            | 类型     | 必填 | 说明                                         |
|----------------|--------|----|--------------------------------------------|
| requestId      | String | 是  | 请求ID                                       |
| conversationId | String | 是  | 会话ID                                       |
| type           | String | 是  | 确认类型: FILE_WRITE/FILE_DELETE/CONTINUE_PLAN |
| message        | String | 是  | 确认消息                                       |
| details        | Object | 否  | 详细信息                                       |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "requestId": "confirm-123",
    "status": "PENDING",
    "expiresAt": "2026-03-21T10:10:00"
  }
}
```

---

### 7.2 提交确认响应

**接口**: `POST /api/ai/confirmations/{requestId}/respond`

**描述**: 用户提交确认响应

**请求参数**:

| 参数名      | 类型      | 必填 | 说明   |
|----------|---------|----|------|
| approved | Boolean | 是  | 是否批准 |
| comment  | String  | 否  | 备注   |

**请求示例**:

```json
{
  "approved": true,
  "comment": "同意执行"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "requestId": "confirm-123",
    "status": "APPROVED",
    "respondedAt": "2026-03-21T10:06:00"
  }
}
```

---

### 7.3 订阅确认请求（WebSocket）

**接口**: `WS /ws/ai/users/{userId}/confirmations`

**描述**: 用户订阅确认请求通知

**消息格式**:

```json
{
  "requestId": "confirm-123",
  "conversationId": "conv-123",
  "type": "FILE_WRITE",
  "message": "AI 想要创建文件 UserController.java，是否允许？",
  "details": {
    "filePath": "src/main/java/UserController.java",
    "operation": "CREATE",
    "size": "2.3 KB"
  },
  "timeout": 300,
  "createdAt": "2026-03-21T10:05:00"
}
```

---

## 8. 会话恢复接口

### 8.1 获取未完成的会话

**接口**: `GET /api/ai/sessions/incomplete`

**描述**: 获取用户未完成的会话（可恢复的会话）

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessions": [
      {
        "conversationId": "conv-123",
        "title": "实现用户管理功能",
        "planId": "plan-123",
        "progress": {
          "currentStep": 3,
          "totalSteps": 6,
          "percentage": 50
        },
        "lastActiveAt": "2026-03-21T10:30:00",
        "status": "ACTIVE"
      }
    ]
  }
}
```

---

### 8.2 恢复会话

**接口**: `POST /api/ai/sessions/{conversationId}/restore`

**描述**: 恢复之前的会话状态

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": "conv-123",
    "restored": true,
    "context": {
      "planId": "plan-123",
      "currentStep": 3,
      "messages": [...]
    }
  }
}
```

---

## 9. Token 使用接口

### 9.1 获取 Token 使用统计

**接口**: `GET /api/ai/tokens/usage`

**描述**: 获取当前用户的 Token 使用统计

**查询参数**:

| 参数名       | 类型     | 必填 | 说明                |
|-----------|--------|----|-------------------|
| startDate | String | 否  | 开始日期 (yyyy-MM-dd) |
| endDate   | String | 否  | 结束日期 (yyyy-MM-dd) |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalInputTokens": 50000,
    "totalOutputTokens": 30000,
    "totalTokens": 80000,
    "totalCost": 12.50,
    "dailyUsage": [
      {
        "date": "2026-03-21",
        "inputTokens": 5000,
        "outputTokens": 3000,
        "totalTokens": 8000,
        "cost": 1.25
      }
    ],
    "modelBreakdown": [
      {
        "modelId": "zhipu",
        "totalTokens": 60000,
        "cost": 9.00
      },
      {
        "modelId": "embedding",
        "totalTokens": 20000,
        "cost": 3.50
      }
    ]
  }
}
```

---

## 10. 健康检查接口

### 10.1 服务健康检查

**接口**: `GET /api/ai/health`

**描述**: 检查 AI 服务健康状态

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "components": {
      "model": {
        "status": "UP",
        "modelId": "zhipu",
        "latency": 150
      },
      "database": {
        "status": "UP"
      },
      "redis": {
        "status": "UP"
      },
      "vectorStore": {
        "status": "UP",
        "documentCount": 1000
      }
    }
  }
}
```

---

## 11. 前端调用示例

### 11.1 发送消息（流式）

```javascript
// 使用 EventSource 接收流式响应
async function sendMessage(message, conversationId) {
  const response = await fetch('/api/ai/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      message,
      conversationId,
      stream: true
    })
  });

  const reader = response.body.getReader();
  const decoder = new TextDecoder();

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    const text = decoder.decode(value);
    const lines = text.split('\n');

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const data = JSON.parse(line.slice(6));
        handleStreamEvent(data);
      }
    }
  }
}

function handleStreamEvent(data) {
  switch (data.type) {
    case 'thought':
      console.log('思考:', data.content);
      break;
    case 'tool_call':
      console.log('调用工具:', data.tool);
      break;
    case 'tool_result':
      console.log('工具结果:', data.result);
      break;
    case 'message':
      appendMessage(data.content);
      break;
    case 'done':
      console.log('完成:', data.conversationId);
      break;
  }
}
```

### 11.2 WebSocket 进度订阅

```javascript
function subscribeProgress(planId, onProgress) {
  const ws = new WebSocket(`ws://localhost:58888/ws/ai/plans/${planId}/progress`);

  ws.onopen = () => {
    console.log('WebSocket 已连接');
  };

  ws.onmessage = (event) => {
    const progress = JSON.parse(event.data);
    onProgress(progress);
  };

  ws.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };

  ws.onclose = () => {
    console.log('WebSocket 已关闭');
  };

  return ws;
}

// 使用示例
const ws = subscribeProgress('plan-123', (progress) => {
  updateProgressBar(progress.percentage);
  updateStatusText(progress.message);
});
```

---

## 12. 版本记录

| 版本   | 日期         | 作者     | 描述 |
|------|------------|--------|----|
| v1.0 | 2026-03-21 | Claude | 初稿 |
