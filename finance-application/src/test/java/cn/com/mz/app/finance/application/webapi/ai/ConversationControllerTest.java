package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.dto.request.ConversationCreateRequest;
import cn.com.mz.app.finance.ai.dto.response.ConversationResponse;
import cn.com.mz.app.finance.ai.dto.response.MessageHistoryResponse;
import cn.com.mz.app.finance.ai.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ConversationController 单元测试
 *
 * @author mz
 */
@DisplayName("ConversationController 单元测试")
class ConversationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ConversationController controller = new ConversationController(conversationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("测试创建会话 - 成功")
    void testCreateConversation_Success() throws Exception {
        // 准备测试数据
        ConversationCreateRequest request = ConversationCreateRequest.builder()
                .title("测试会话")
                .agentType("chat")
                .modelId("zhipu")
                .build();

        ConversationResponse response = ConversationResponse.builder()
                .conversationId("conv-test-001")
                .title("测试会话")
                .agentType("chat")
                .modelId("zhipu")
                .status("ACTIVE")
                .messageCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        when(conversationService.createConversation(anyString(), anyString(), anyString()))
                .thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/ai/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-test-001"))
                .andExpect(jsonPath("$.data.title").value("测试会话"))
                .andExpect(jsonPath("$.data.agentType").value("chat"));
    }

    @Test
    @DisplayName("测试创建会话 - 使用默认值")
    void testCreateConversation_WithDefaults() throws Exception {
        ConversationCreateRequest request = ConversationCreateRequest.builder()
                .title("默认会话")
                .build();

        ConversationResponse response = ConversationResponse.builder()
                .conversationId("conv-test-002")
                .title("默认会话")
                .agentType("chat")
                .modelId("zhipu")
                .status("ACTIVE")
                .build();

        when(conversationService.createConversation("默认会话", null, null))
                .thenReturn(response);

        mockMvc.perform(post("/api/ai/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试获取会话列表 - 成功")
    void testGetConversations_Success() throws Exception {
        List<ConversationResponse> conversations = List.of(
                ConversationResponse.builder()
                        .conversationId("conv-001")
                        .title("会话1")
                        .agentType("chat")
                        .status("ACTIVE")
                        .messageCount(5)
                        .build(),
                ConversationResponse.builder()
                        .conversationId("conv-002")
                        .title("会话2")
                        .agentType("code")
                        .status("ACTIVE")
                        .messageCount(3)
                        .build()
        );

        when(conversationService.getConversations(isNull(), isNull(), eq(1), eq(20)))
                .thenReturn(conversations);

        mockMvc.perform(get("/api/ai/conversations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversations").isArray())
                .andExpect(jsonPath("$.data.conversations.length()").value(2))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(20));
    }

    @Test
    @DisplayName("测试获取会话列表 - 带过滤条件")
    void testGetConversations_WithFilters() throws Exception {
        List<ConversationResponse> conversations = List.of(
                ConversationResponse.builder()
                        .conversationId("conv-003")
                        .title("代码会话")
                        .agentType("code")
                        .status("ACTIVE")
                        .build()
        );

        when(conversationService.getConversations("ACTIVE", "code", 1, 10))
                .thenReturn(conversations);

        mockMvc.perform(get("/api/ai/conversations")
                        .param("status", "ACTIVE")
                        .param("agentType", "code")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversations.length()").value(1));
    }

    @Test
    @DisplayName("测试获取会话详情 - 成功")
    void testGetConversation_Success() throws Exception {
        ConversationResponse response = ConversationResponse.builder()
                .conversationId("conv-001")
                .title("测试会话")
                .agentType("chat")
                .modelId("zhipu")
                .status("ACTIVE")
                .messageCount(10)
                .build();

        when(conversationService.getConversation("conv-001")).thenReturn(response);

        mockMvc.perform(get("/api/ai/conversations/conv-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-001"))
                .andExpect(jsonPath("$.data.title").value("测试会话"));
    }

    @Test
    @DisplayName("测试删除会话 - 成功")
    void testDeleteConversation_Success() throws Exception {
        mockMvc.perform(delete("/api/ai/conversations/conv-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(conversationService).deleteConversation("conv-001");
    }

    @Test
    @DisplayName("测试获取消息历史 - 成功")
    void testGetMessageHistory_Success() throws Exception {
        List<MessageHistoryResponse.MessageItem> messages = List.of(
                MessageHistoryResponse.MessageItem.builder()
                        .messageId("msg-001")
                        .role("USER")
                        .content("你好")
                        .createdAt(LocalDateTime.now())
                        .build(),
                MessageHistoryResponse.MessageItem.builder()
                        .messageId("msg-002")
                        .role("ASSISTANT")
                        .content("你好！有什么可以帮助你的？")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        MessageHistoryResponse response = MessageHistoryResponse.builder()
                .conversationId("conv-001")
                .title("测试会话")
                .messages(messages)
                .total(2L)
                .page(1)
                .size(20)
                .build();

        when(conversationService.getMessageHistory("conv-001", 1, 20, "asc"))
                .thenReturn(response);

        mockMvc.perform(get("/api/ai/conversations/conv-001/messages")
                        .param("page", "1")
                        .param("size", "20")
                        .param("order", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.conversationId").value("conv-001"))
                .andExpect(jsonPath("$.data.messages").isArray())
                .andExpect(jsonPath("$.data.messages.length()").value(2));
    }

    @Test
    @DisplayName("测试获取消息历史 - 降序排列")
    void testGetMessageHistory_DescOrder() throws Exception {
        MessageHistoryResponse response = MessageHistoryResponse.builder()
                .conversationId("conv-001")
                .messages(List.of())
                .total(0L)
                .page(1)
                .size(20)
                .build();

        when(conversationService.getMessageHistory("conv-001", 1, 20, "desc"))
                .thenReturn(response);

        mockMvc.perform(get("/api/ai/conversations/conv-001/messages")
                        .param("order", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
