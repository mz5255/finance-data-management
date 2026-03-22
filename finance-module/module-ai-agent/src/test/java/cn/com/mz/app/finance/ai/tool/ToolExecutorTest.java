package cn.com.mz.app.finance.ai.tool;

import cn.com.mz.app.finance.ai.exception.AgentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ToolExecutor 单元测试
 *
 * @author mz
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ToolExecutor 单元测试")
class ToolExecutorTest {

    @Mock
    private ToolRegistry toolRegistry;

    @Mock
    private Tool tool;

    private ToolExecutor toolExecutor;

    @BeforeEach
    void setUp() {
        toolExecutor = new ToolExecutor(toolRegistry);
    }

    @Test
    @DisplayName("测试执行工具 - 成功")
    void testExecute_Success() {
        // 准备 Mock
        when(tool.getParameters()).thenReturn(Map.of());
        when(tool.execute(any())).thenReturn(ToolResult.success(Map.of("result", 3)));
        when(toolRegistry.getTool("calculator")).thenReturn(tool);

        // 执行
        ToolResult result = toolExecutor.execute("calculator", Map.of("expression", "1+2"));

        // 验证
        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals(3, data.get("result"));

        assertNotNull(result.getDuration());
        verify(tool).execute(any());
    }

    @Test
    @DisplayName("测试执行工具 - 工具不存在")
    void testExecute_ToolNotFound() {
        when(toolRegistry.getTool("unknown")).thenReturn(null);

        assertThrows(AgentException.class, () -> {
            toolExecutor.execute("unknown", Map.of());
        });
    }

    @Test
    @DisplayName("测试执行工具 - 工具执行异常")
    void testExecute_ToolException() {
        when(tool.getParameters()).thenReturn(Map.of());
        when(tool.execute(any())).thenThrow(new RuntimeException("Tool execution failed"));
        when(toolRegistry.getTool("calculator")).thenReturn(tool);

        ToolResult result = toolExecutor.execute("calculator", Map.of());

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("Tool execution failed"));
    }

    @Test
    @DisplayName("测试批量执行工具")
    void testExecuteBatch() {
        when(tool.getParameters()).thenReturn(Map.of());
        when(tool.execute(any())).thenReturn(ToolResult.success(Map.of()));
        when(toolRegistry.getTool("tool1")).thenReturn(tool);
        when(toolRegistry.getTool("tool2")).thenReturn(tool);

        List<ToolExecutor.ToolExecution> executions = List.of(
                new ToolExecutor.ToolExecution("tool1", Map.of()),
                new ToolExecutor.ToolExecution("tool2", Map.of())
        );

        List<ToolResult> results = toolExecutor.executeBatch(executions);

        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
    }

    @Test
    @DisplayName("测试检查工具是否需要确认 - 需要确认")
    void testNeedsConfirmation_True() {
        when(tool.requireConfirmation()).thenReturn(true);
        when(toolRegistry.getTool("file_writer")).thenReturn(tool);

        assertTrue(toolExecutor.needsConfirmation("file_writer"));
    }

    @Test
    @DisplayName("测试检查工具是否需要确认 - 不需要确认")
    void testNeedsConfirmation_False() {
        when(tool.requireConfirmation()).thenReturn(false);
        when(toolRegistry.getTool("calculator")).thenReturn(tool);

        assertFalse(toolExecutor.needsConfirmation("calculator"));
    }

    @Test
    @DisplayName("测试检查工具是否需要确认 - 工具不存在")
    void testNeedsConfirmation_ToolNotFound() {
        when(toolRegistry.getTool("unknown")).thenReturn(null);

        assertFalse(toolExecutor.needsConfirmation("unknown"));
    }

    @Test
    @DisplayName("测试获取工具信息")
    void testGetToolInfo() {
        when(tool.getName()).thenReturn("calculator");
        when(tool.getDescription()).thenReturn("计算器");
        when(tool.getCategory()).thenReturn("utility");
        when(tool.getRiskLevel()).thenReturn(Tool.RiskLevel.LOW);
        when(tool.requireConfirmation()).thenReturn(false);
        when(tool.getParameters()).thenReturn(Map.of("type", "object"));
        when(toolRegistry.getTool("calculator")).thenReturn(tool);

        Map<String, Object> info = toolExecutor.getToolInfo("calculator");

        assertNotNull(info);
        assertEquals("calculator", info.get("name"));
        assertEquals("计算器", info.get("description"));
        assertEquals("utility", info.get("category"));
        assertEquals("LOW", info.get("riskLevel"));
        assertEquals(false, info.get("requireConfirmation"));
    }

    @Test
    @DisplayName("测试获取工具信息 - 工具不存在")
    void testGetToolInfo_ToolNotFound() {
        when(toolRegistry.getTool("unknown")).thenReturn(null);

        assertNull(toolExecutor.getToolInfo("unknown"));
    }

    @Test
    @DisplayName("测试执行工具 - 设置执行时长")
    void testExecute_SetsDuration() throws InterruptedException {
        // 模拟耗时操作
        when(tool.getParameters()).thenReturn(Map.of());
        when(tool.execute(any())).thenAnswer(invocation -> {
            Thread.sleep(10); // 模拟耗时
            return ToolResult.success(Map.of());
        });
        when(toolRegistry.getTool("calculator")).thenReturn(tool);

        ToolResult result = toolExecutor.execute("calculator", Map.of());

        assertTrue(result.getDuration() >= 10);
    }
}
