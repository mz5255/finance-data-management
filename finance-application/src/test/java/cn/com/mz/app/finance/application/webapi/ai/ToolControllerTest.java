package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.tool.Tool;
import cn.com.mz.app.finance.ai.tool.ToolExecutor;
import cn.com.mz.app.finance.ai.tool.ToolRegistry;
import cn.com.mz.app.finance.ai.tool.ToolResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ToolController 单元测试
 *
 * @author mz
 */
@DisplayName("ToolController 单元测试")
class ToolControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    @Mock
    private ToolRegistry toolRegistry;
    @Mock
    private ToolExecutor toolExecutor;
    @Mock
    private Tool calculatorTool;
    @Mock
    private Tool searchTool;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ToolController controller = new ToolController(toolRegistry, toolExecutor);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        setupMockTools();
    }

    private void setupMockTools() {
        // Calculator 工具
        when(calculatorTool.getName()).thenReturn("calculator");
        when(calculatorTool.getDescription()).thenReturn("执行数学表达式计算");
        when(calculatorTool.getCategory()).thenReturn("utility");
        when(calculatorTool.getRiskLevel()).thenReturn(Tool.RiskLevel.LOW);
        when(calculatorTool.requireConfirmation()).thenReturn(false);
        when(calculatorTool.getParameters()).thenReturn(Map.of(
                "type", "object",
                "properties", Map.of("expression", Map.of("type", "string"))
        ));

        // Search 工具
        when(searchTool.getName()).thenReturn("search");
        when(searchTool.getDescription()).thenReturn("执行搜索查询");
        when(searchTool.getCategory()).thenReturn("utility");
        when(searchTool.getRiskLevel()).thenReturn(Tool.RiskLevel.LOW);
        when(searchTool.requireConfirmation()).thenReturn(false);
        when(searchTool.getParameters()).thenReturn(Map.of(
                "type", "object",
                "properties", Map.of("query", Map.of("type", "string"))
        ));
    }

    @Test
    @DisplayName("测试获取工具列表 - 成功")
    void testGetTools_Success() throws Exception {
        when(toolRegistry.getAllTools()).thenReturn(Set.of(calculatorTool, searchTool));

        mockMvc.perform(get("/api/ai/tools"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.tools").isArray())
                .andExpect(jsonPath("$.data.tools.length()").value(2));
    }

    @Test
    @DisplayName("测试获取工具列表 - 按分类过滤")
    void testGetTools_WithCategory() throws Exception {
        when(toolRegistry.getToolsByCategory("utility")).thenReturn(List.of(calculatorTool));

        mockMvc.perform(get("/api/ai/tools")
                        .param("category", "utility"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.tools.length()").value(1))
                .andExpect(jsonPath("$.data.tools[0].name").value("calculator"));
    }

    @Test
    @DisplayName("测试获取工具详情 - 成功")
    void testGetTool_Success() throws Exception {
        when(toolRegistry.getTool("calculator")).thenReturn(calculatorTool);

        mockMvc.perform(get("/api/ai/tools/calculator"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("calculator"))
                .andExpect(jsonPath("$.data.description").value("执行数学表达式计算"))
                .andExpect(jsonPath("$.data.category").value("utility"))
                .andExpect(jsonPath("$.data.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.data.requireConfirmation").value(false));
    }

    @Test
    @DisplayName("测试获取工具详情 - 工具不存在")
    void testGetTool_NotFound() throws Exception {
        when(toolRegistry.getTool("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/ai/tools/unknown"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Tool not found: unknown"));
    }

    @Test
    @DisplayName("测试执行工具 - 计算器成功")
    void testExecuteTool_CalculatorSuccess() throws Exception {
        Map<String, Object> params = Map.of("expression", "1 + 2");
        ToolResult result = ToolResult.success(Map.of("result", 3));

        when(toolExecutor.execute("calculator", params)).thenReturn(result);

        mockMvc.perform(post("/api/ai/tools/calculator/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.data.result").value(3));
    }

    @Test
    @DisplayName("测试执行工具 - 搜索成功")
    void testExecuteTool_SearchSuccess() throws Exception {
        Map<String, Object> params = Map.of("query", "Spring Boot");
        ToolResult result = ToolResult.success(Map.of(
                "query", "Spring Boot",
                "total", 10,
                "results", List.of()
        ));

        when(toolExecutor.execute("search", params)).thenReturn(result);

        mockMvc.perform(post("/api/ai/tools/search/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    @DisplayName("测试执行工具 - 执行失败")
    void testExecuteTool_Failure() throws Exception {
        Map<String, Object> params = Map.of("expression", "invalid");
        ToolResult result = ToolResult.error("表达式格式不正确");

        when(toolExecutor.execute("calculator", params)).thenReturn(result);

        mockMvc.perform(post("/api/ai/tools/calculator/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.error").value("表达式格式不正确"));
    }

    @Test
    @DisplayName("测试执行工具 - 空参数")
    void testExecuteTool_EmptyParams() throws Exception {
        ToolResult result = ToolResult.error("缺少必填参数");
        when(toolExecutor.execute(anyString(), anyMap())).thenReturn(result);

        mockMvc.perform(post("/api/ai/tools/calculator/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("测试工具响应包含正确的风险等级")
    void testGetTool_VerifyRiskLevel() throws Exception {
        // 创建一个高风险工具
        Tool highRiskTool = createMockTool("file_writer", "写入文件", "file", Tool.RiskLevel.HIGH, true);
        when(toolRegistry.getTool("file_writer")).thenReturn(highRiskTool);

        mockMvc.perform(get("/api/ai/tools/file_writer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.data.requireConfirmation").value(true));
    }

    private Tool createMockTool(String name, String description, String category,
                                Tool.RiskLevel riskLevel, boolean requireConfirmation) {
        Tool tool = org.mockito.Mockito.mock(Tool.class);
        when(tool.getName()).thenReturn(name);
        when(tool.getDescription()).thenReturn(description);
        when(tool.getCategory()).thenReturn(category);
        when(tool.getRiskLevel()).thenReturn(riskLevel);
        when(tool.requireConfirmation()).thenReturn(requireConfirmation);
        when(tool.getParameters()).thenReturn(Map.of());
        return tool;
    }
}
