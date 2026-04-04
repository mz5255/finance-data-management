package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.model.ModelFactory;
import cn.com.mz.app.finance.ai.tool.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HealthController 单元测试
 *
 * @author mz
 */
@DisplayName("HealthController 单元测试")
class HealthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ModelFactory modelFactory;

    @Mock
    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        HealthController controller = new HealthController(modelFactory, toolRegistry);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("测试健康检查 - 所有组件正常")
    void testHealth_AllComponentsUp() throws Exception {
        // Mock 行为
        when(modelFactory.isModelAvailable("zhipu")).thenReturn(true);
        when(toolRegistry.getToolNames()).thenReturn(Set.of("calculator", "file_reader", "search"));

        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.components.model.status").value("UP"))
                .andExpect(jsonPath("$.data.components.tools.status").value("UP"))
                .andExpect(jsonPath("$.data.components.tools.documentCount").value(3));
    }

    @Test
    @DisplayName("测试健康检查 - 模型不可用")
    void testHealth_ModelDown() throws Exception {
        // Mock 行为
        when(modelFactory.isModelAvailable("zhipu")).thenReturn(false);
        when(toolRegistry.getToolNames()).thenReturn(Set.of("calculator"));

        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DOWN"))
                .andExpect(jsonPath("$.data.components.model.status").value("DOWN"));
    }

    @Test
    @DisplayName("测试健康检查 - 模型检查异常")
    void testHealth_ModelException() throws Exception {
        // Mock 行为 - 模拟异常
        when(modelFactory.isModelAvailable("zhipu")).thenThrow(new RuntimeException("Model check failed"));
        when(toolRegistry.getToolNames()).thenReturn(Set.of("calculator"));

        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DOWN"))
                .andExpect(jsonPath("$.data.components.model.status").value("DOWN"));
    }

    @Test
    @DisplayName("测试健康检查 - 工具检查异常")
    void testHealth_ToolsException() throws Exception {
        // Mock 行为
        when(modelFactory.isModelAvailable("zhipu")).thenReturn(true);
        when(toolRegistry.getToolNames()).thenThrow(new RuntimeException("Tools check failed"));

        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DOWN"))
                .andExpect(jsonPath("$.data.components.tools.status").value("DOWN"));
    }

    @Test
    @DisplayName("测试健康检查 - 所有组件异常")
    void testHealth_AllComponentsDown() throws Exception {
        // Mock 行为
        when(modelFactory.isModelAvailable("zhipu")).thenThrow(new RuntimeException("Model error"));
        when(toolRegistry.getToolNames()).thenThrow(new RuntimeException("Tools error"));

        mockMvc.perform(get("/api/ai/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DOWN"))
                .andExpect(jsonPath("$.data.components.model.status").value("DOWN"))
                .andExpect(jsonPath("$.data.components.tools.status").value("DOWN"));
    }
}
