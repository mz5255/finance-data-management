package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.AgentFactory;
import cn.com.mz.app.finance.ai.dto.request.ChatRequest;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChatController 单元测试
 *
 * @author mz
 */
@DisplayName("ChatController 单元测试")
class ChatControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private AgentFactory agentFactory;
    @Mock
    private Agent agent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ChatController controller = new ChatController(agentFactory);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("测试发送消息 - 成功")
    void testChat_Success() throws Exception {
        // 准备测试数据
        ChatRequest request = ChatRequest.builder()
                .message("你好，请介绍一下你自己")
                .agentType("chat")
                .stream(false)
                .build();

        ChatResponse response = ChatResponse.builder()
                .conversationId("conv-test-001")
                .messageId("msg-test-001")
                .content("你好！我是一个智能助手。")
                .build();

        // Mock 行为
        when(agentFactory.createByType(anyString())).thenReturn(agent);
        when(agent.chat(any(), anyString())).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-test-001"))
                .andExpect(jsonPath("$.data.content").value("你好！我是一个智能助手。"));
    }

    @Test
    @DisplayName("测试发送消息 - 使用 code agent")
    void testChat_WithCodeAgent() throws Exception {
        // 准备测试数据
        ChatRequest request = ChatRequest.builder()
                .message("帮我写一个 Hello World 程序")
                .agentType("code")
                .conversationId("conv-existing-001")
                .stream(false)
                .build();

        ChatResponse response = ChatResponse.builder()
                .conversationId("conv-existing-001")
                .messageId("msg-002")
                .content("```java\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}\n```")
                .build();

        // Mock 行为
        when(agentFactory.createByType("code")).thenReturn(agent);
        when(agent.chat(any(), anyString())).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-existing-001"));
    }

    @Test
    @DisplayName("测试发送消息 - 使用 data agent")
    void testChat_WithDataAgent() throws Exception {
        // 准备测试数据
        ChatRequest request = ChatRequest.builder()
                .message("帮我分析上个月的财务数据")
                .agentType("data")
                .stream(false)
                .build();

        ChatResponse response = ChatResponse.builder()
                .conversationId("conv-test-003")
                .messageId("msg-003")
                .content("根据分析，上个月的财务数据如下...")
                .build();

        // Mock 行为
        when(agentFactory.createByType("data")).thenReturn(agent);
        when(agent.chat(any(), any())).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").value("根据分析，上个月的财务数据如下..."));
    }

    @Test
    @DisplayName("测试流式对话")
    void testChatStream() throws Exception {
        // 准备测试数据
        ChatRequest request = ChatRequest.builder()
                .message("讲一个故事")
                .agentType("chat")
                .stream(true)
                .build();

        // Mock 流式响应
        ChatResponse chunk1 = ChatResponse.builder()
                .conversationId("conv-stream-001")
                .messageId("msg-stream-001")
                .content("从前")
                .build();

        ChatResponse chunk2 = ChatResponse.builder()
                .conversationId("conv-stream-001")
                .content("有座山")
                .build();

        when(agentFactory.createByType("chat")).thenReturn(agent);
        when(agent.chatStream(any(), any())).thenReturn(Flux.just(chunk1, chunk2));

        // 执行测试
        mockMvc.perform(post("/api/ai/chat/stream")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
