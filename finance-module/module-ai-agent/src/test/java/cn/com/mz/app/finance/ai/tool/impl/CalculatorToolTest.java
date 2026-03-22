package cn.com.mz.app.finance.ai.tool.impl;

import cn.com.mz.app.finance.ai.tool.ToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CalculatorTool 单元测试
 *
 * @author mz
 */
@DisplayName("CalculatorTool 单元测试")
class CalculatorToolTest {

    private CalculatorTool calculatorTool;

    @BeforeEach
    void setUp() {
        calculatorTool = new CalculatorTool();
    }

    @Test
    @DisplayName("测试工具基本信息")
    void testToolBasicInfo() {
        assertEquals("calculator", calculatorTool.getName());
        assertEquals("执行数学表达式计算，支持加减乘除和括号", calculatorTool.getDescription());
        assertEquals("utility", calculatorTool.getCategory());
        assertEquals(CalculatorTool.RiskLevel.LOW, calculatorTool.getRiskLevel());
        assertFalse(calculatorTool.requireConfirmation());
    }

    @Test
    @DisplayName("测试工具参数定义")
    void testToolParameters() {
        Map<String, Object> params = calculatorTool.getParameters();
        assertNotNull(params);
        assertEquals("object", params.get("type"));
        assertTrue(params.containsKey("properties"));
        assertTrue(params.containsKey("required"));
    }

    @Test
    @DisplayName("测试简单加法")
    void testSimpleAddition() {
        Map<String, Object> params = Map.of("expression", "1 + 2");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("3", data.get("result"));
    }

    @Test
    @DisplayName("测试简单减法")
    void testSimpleSubtraction() {
        Map<String, Object> params = Map.of("expression", "10 - 3");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("7", data.get("result"));
    }

    @Test
    @DisplayName("测试简单乘法")
    void testSimpleMultiplication() {
        Map<String, Object> params = Map.of("expression", "4 * 5");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("20", data.get("result"));
    }

    @Test
    @DisplayName("测试简单除法")
    void testSimpleDivision() {
        Map<String, Object> params = Map.of("expression", "20 / 4");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("5", data.get("result"));
    }

    @Test
    @DisplayName("测试括号表达式")
    void testParenthesesExpression() {
        Map<String, Object> params = Map.of("expression", "(1 + 2) * 3");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertEquals("9", data.get("result"));
    }

    @Test
    @DisplayName("测试复杂表达式")
    void testComplexExpression() {
        Map<String, Object> params = Map.of("expression", "2 + 3 * 4");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("result"));
    }

    @Test
    @DisplayName("测试带空格的表达式")
    void testExpressionWithSpaces() {
        Map<String, Object> params = Map.of("expression", "  1  +  2  ");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("测试空表达式")
    void testEmptyExpression() {
        Map<String, Object> params = Map.of("expression", "");
        ToolResult result = calculatorTool.execute(params);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    @DisplayName("测试空白表达式")
    void testBlankExpression() {
        Map<String, Object> params = Map.of("expression", "   ");
        ToolResult result = calculatorTool.execute(params);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }

    @Test
    @DisplayName("测试非法字符 - 包含字母")
    void testIllegalCharacters() {
        Map<String, Object> params = Map.of("expression", "1 + abc");
        ToolResult result = calculatorTool.execute(params);

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("非法字符") || result.getError().contains("格式"));
    }

    @Test
    @DisplayName("测试包含代码注入尝试")
    void testCodeInjectionAttempt() {
        Map<String, Object> params = Map.of("expression", "System.exit(0)");
        ToolResult result = calculatorTool.execute(params);

        assertFalse(result.isSuccess());
        assertTrue(result.getError().contains("非法字符") || result.getError().contains("格式"));
    }

    @Test
    @DisplayName("测试浮点数运算")
    void testFloatingPointCalculation() {
        Map<String, Object> params = Map.of("expression", "1.5 + 2.5");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        assertNotNull(data.get("result"));
    }

    @Test
    @DisplayName("测试结果包含原始表达式")
    void testResultContainsOriginalExpression() {
        Map<String, Object> params = Map.of("expression", "1 + 2");
        ToolResult result = calculatorTool.execute(params);

        assertTrue(result.isSuccess());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.getData();
        // 表达式会保留空格（CalculatorTool 内部只做了 trim）
        assertNotNull(data.get("expression"));
        assertTrue(data.get("expression").toString().contains("1"));
        assertTrue(data.get("expression").toString().contains("2"));
    }

    @Test
    @DisplayName("测试缺少 expression 参数")
    void testMissingExpressionParameter() {
        Map<String, Object> params = Map.of();
        ToolResult result = calculatorTool.execute(params);

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}
