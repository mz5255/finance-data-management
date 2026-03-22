package cn.com.mz.app.finance.ai.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolRegistry 单元测试
 *
 * @author mz
 */
@DisplayName("ToolRegistry 单元测试")
class ToolRegistryTest {

    private ToolRegistry toolRegistry;

    @BeforeEach
    void setUp() {
        toolRegistry = new ToolRegistry(List.of());
        toolRegistry.init();
    }

    @Test
    @DisplayName("测试注册工具")
    void testRegisterTool() {
        Tool tool = createMockTool("test_tool", "测试工具", "test");
        toolRegistry.register(tool);

        assertTrue(toolRegistry.hasTool("test_tool"));
        assertEquals(tool, toolRegistry.getTool("test_tool"));
    }

    @Test
    @DisplayName("测试注销工具")
    void testUnregisterTool() {
        Tool tool = createMockTool("test_tool", "测试工具", "test");
        toolRegistry.register(tool);

        assertTrue(toolRegistry.hasTool("test_tool"));

        toolRegistry.unregister("test_tool");

        assertFalse(toolRegistry.hasTool("test_tool"));
        assertNull(toolRegistry.getTool("test_tool"));
    }

    @Test
    @DisplayName("测试获取所有工具名称")
    void testGetToolNames() {
        Tool tool1 = createMockTool("tool1", "工具1", "cat1");
        Tool tool2 = createMockTool("tool2", "工具2", "cat2");

        toolRegistry.register(tool1);
        toolRegistry.register(tool2);

        assertEquals(2, toolRegistry.getToolNames().size());
        assertTrue(toolRegistry.getToolNames().contains("tool1"));
        assertTrue(toolRegistry.getToolNames().contains("tool2"));
    }

    @Test
    @DisplayName("测试获取所有工具")
    void testGetAllTools() {
        Tool tool1 = createMockTool("tool1", "工具1", "cat1");
        Tool tool2 = createMockTool("tool2", "工具2", "cat2");

        toolRegistry.register(tool1);
        toolRegistry.register(tool2);

        assertEquals(2, toolRegistry.getAllTools().size());
    }

    @Test
    @DisplayName("测试根据分类获取工具")
    void testGetToolsByCategory() {
        Tool tool1 = createMockTool("tool1", "工具1", "utility");
        Tool tool2 = createMockTool("tool2", "工具2", "file");
        Tool tool3 = createMockTool("tool3", "工具3", "utility");

        toolRegistry.register(tool1);
        toolRegistry.register(tool2);
        toolRegistry.register(tool3);

        List<Tool> utilityTools = toolRegistry.getToolsByCategory("utility");
        assertEquals(2, utilityTools.size());

        List<Tool> fileTools = toolRegistry.getToolsByCategory("file");
        assertEquals(1, fileTools.size());
    }

    @Test
    @DisplayName("测试获取工具信息列表")
    void testGetToolInfoList() {
        Tool tool = createMockTool("calculator", "计算器", "utility");
        toolRegistry.register(tool);

        List<Map<String, Object>> infoList = toolRegistry.getToolInfoList();
        assertEquals(1, infoList.size());

        Map<String, Object> info = infoList.get(0);
        assertEquals("calculator", info.get("name"));
        assertEquals("计算器", info.get("description"));
        assertEquals("utility", info.get("category"));
    }

    @Test
    @DisplayName("测试覆盖同名工具")
    void testOverrideTool() {
        Tool tool1 = createMockTool("test", "工具1", "cat1");
        Tool tool2 = createMockTool("test", "工具2", "cat2");

        toolRegistry.register(tool1);
        toolRegistry.register(tool2);

        assertEquals(1, toolRegistry.getToolNames().size());
        assertEquals("工具2", toolRegistry.getTool("test").getDescription());
    }

    @Test
    @DisplayName("测试获取不存在的工具")
    void testGetNonExistentTool() {
        assertNull(toolRegistry.getTool("non_existent"));
        assertFalse(toolRegistry.hasTool("non_existent"));
    }

    @Test
    @DisplayName("测试空注册表")
    void testEmptyRegistry() {
        assertEquals(0, toolRegistry.getToolNames().size());
        assertEquals(0, toolRegistry.getAllTools().size());
        assertEquals(0, toolRegistry.getToolInfoList().size());
    }

    /**
     * 创建模拟工具
     */
    private Tool createMockTool(String name, String description, String category) {
        return new Tool() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getCategory() {
                return category;
            }

            @Override
            public RiskLevel getRiskLevel() {
                return RiskLevel.LOW;
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of();
            }

            @Override
            public ToolResult execute(Map<String, Object> params) {
                return ToolResult.success(Map.of());
            }
        };
    }
}
