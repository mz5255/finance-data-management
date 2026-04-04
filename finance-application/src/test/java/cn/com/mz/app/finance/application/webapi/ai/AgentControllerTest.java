package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.agent.AgentFactory;
import cn.com.mz.app.finance.ai.agent.strategy.AgentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AgentController 单元测试
 *
 * @author mz
 */
@DisplayName("AgentController 单元测试")
class AgentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AgentFactory agentFactory;

    @Mock
    private AgentStrategy chatStrategy;

    @Mock
    private AgentStrategy codeStrategy;

    @Mock
    private AgentStrategy dataStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AgentController controller = new AgentController(agentFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 配置 Mock 策略
        setupMockStrategies();
    }

    private void setupMockStrategies() {
        // Chat Agent 策略
        when(chatStrategy.getName()).thenReturn("通用助手");
        when(chatStrategy.getDescription()).thenReturn("通用对话 Agent，适合日常问答和简单任务");
        when(chatStrategy.getAvailableTools()).thenReturn(List.of("search", "calculator"));
        when(chatStrategy.getIcon()).thenReturn("chat-icon");
        when(chatStrategy.getSystemPrompt()).thenReturn("你是一个智能助手...");

        // Code Agent 策略
        when(codeStrategy.getName()).thenReturn("开发助手");
        when(codeStrategy.getDescription()).thenReturn("代码开发和项目分析 Agent");
        when(codeStrategy.getAvailableTools()).thenReturn(List.of("file_reader", "file_writer", "code_search"));
        when(codeStrategy.getIcon()).thenReturn("code-icon");
        when(codeStrategy.getSystemPrompt()).thenReturn("你是一个专业的代码助手...");

        // Data Agent 策略
        when(dataStrategy.getName()).thenReturn("数据分析");
        when(dataStrategy.getDescription()).thenReturn("财务数据分析和报表生成 Agent");
        when(dataStrategy.getAvailableTools()).thenReturn(List.of("database_query", "calculator", "chart_generator"));
        when(dataStrategy.getIcon()).thenReturn("data-icon");
        when(dataStrategy.getSystemPrompt()).thenReturn("你是一个专业的财务数据分析助手...");

        // AgentFactory 返回策略
        when(agentFactory.getStrategy("chat")).thenReturn(chatStrategy);
        when(agentFactory.getStrategy("code")).thenReturn(codeStrategy);
        when(agentFactory.getStrategy("data")).thenReturn(dataStrategy);
        when(agentFactory.getSupportedTypes()).thenReturn(List.of("chat", "code", "data"));
    }

    @Test
    @DisplayName("测试获取 Agent 列表 - 成功")
    void testGetAgents_Success() throws Exception {
        mockMvc.perform(get("/api/ai/agents"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.agents").isArray())
                .andExpect(jsonPath("$.data.agents.length()").value(3))
                .andExpect(jsonPath("$.data.agents[0].type").value("chat"))
                .andExpect(jsonPath("$.data.agents[1].type").value("code"))
                .andExpect(jsonPath("$.data.agents[2].type").value("data"));
    }

    @Test
    @DisplayName("测试获取 Agent 列表 - 验证 chat agent 信息")
    void testGetAgents_VerifyChatAgentInfo() throws Exception {
        mockMvc.perform(get("/api/ai/agents"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.agents[?(@.type=='chat')].name").value("通用助手"))
                .andExpect(jsonPath("$.data.agents[?(@.type=='chat')].description").value("通用对话 Agent，适合日常问答和简单任务"));
    }

    @Test
    @DisplayName("测试获取 Agent 详情 - chat agent")
    void testGetAgent_ChatAgent() throws Exception {
        mockMvc.perform(get("/api/ai/agents/chat"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("chat"))
                .andExpect(jsonPath("$.data.name").value("通用助手"))
                .andExpect(jsonPath("$.data.description").value("通用对话 Agent，适合日常问答和简单任务"))
                .andExpect(jsonPath("$.data.systemPrompt").value("你是一个智能助手..."))
                .andExpect(jsonPath("$.data.tools").isArray())
                .andExpect(jsonPath("$.data.tools.length()").value(2));
    }

    @Test
    @DisplayName("测试获取 Agent 详情 - code agent")
    void testGetAgent_CodeAgent() throws Exception {
        mockMvc.perform(get("/api/ai/agents/code"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("code"))
                .andExpect(jsonPath("$.data.name").value("开发助手"))
                .andExpect(jsonPath("$.data.tools").isArray())
                .andExpect(jsonPath("$.data.tools.length()").value(3));
    }

    @Test
    @DisplayName("测试获取 Agent 详情 - data agent")
    void testGetAgent_DataAgent() throws Exception {
        mockMvc.perform(get("/api/ai/agents/data"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("data"))
                .andExpect(jsonPath("$.data.name").value("数据分析"));
    }

    @Test
    @DisplayName("测试获取 Agent 详情 - 不存在的 agent")
    void testGetAgent_NotFound() throws Exception {
        when(agentFactory.getStrategy("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/ai/agents/unknown"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Agent not found: unknown"));
    }
}
