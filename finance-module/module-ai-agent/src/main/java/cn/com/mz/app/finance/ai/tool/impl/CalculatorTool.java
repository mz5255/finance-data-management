package cn.com.mz.app.finance.ai.tool.impl;

import cn.com.mz.app.finance.ai.tool.Tool;
import cn.com.mz.app.finance.ai.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算器工具
 * 执行数学表达式计算
 *
 * @author mz
 */
@Component
public class CalculatorTool implements Tool {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[\\d+\\-*/.\\s()]+$");
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile("[a-zA-Z]");

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "执行数学表达式计算，支持加减乘除和括号";
    }

    @Override
    public String getCategory() {
        return "utility";
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.LOW;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("expression", Map.of(
                "type", "string",
                "description", "数学表达式，如: (1 + 2) * 3"
        ));

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("expression")
        );
    }

    @Override
    public ToolResult execute(Map<String, Object> params) {
        String expression = (String) params.get("expression");
        if (expression == null || expression.isBlank()) {
            return ToolResult.error("表达式不能为空");
        }

        // 安全检查
        expression = expression.trim();
        if (DANGEROUS_PATTERN.matcher(expression).find()) {
            return ToolResult.error("表达式包含非法字符");
        }

        if (!NUMBER_PATTERN.matcher(expression).matches()) {
            return ToolResult.error("表达式格式不正确");
        }

        try {
            BigDecimal result = evaluateExpression(expression);
            return ToolResult.success(Map.of(
                    "expression", expression,
                    "result", result.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            ));
        } catch (Exception e) {
            return ToolResult.error("计算失败: " + e.getMessage());
        }
    }

    /**
     * 简单的表达式求值（仅支持基本运算）
     */
    private BigDecimal evaluateExpression(String expression) {
        // 这里使用简单的解析，实际项目中可以使用第三方库如 exp4j
        expression = expression.replaceAll("\\s+", "");

        // 处理括号
        while (expression.contains("(")) {
            int start = expression.lastIndexOf("(");
            int end = expression.indexOf(")", start);
            String subExpr = expression.substring(start + 1, end);
            BigDecimal subResult = evaluateSimple(subExpr);
            expression = expression.substring(0, start) + subResult + expression.substring(end + 1);
        }

        return evaluateSimple(expression);
    }

    private BigDecimal evaluateSimple(String expression) {
        // 处理乘除
        java.util.regex.Pattern mdPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*([*/])\\s*(\\d+\\.?\\d*)");
        Matcher matcher = mdPattern.matcher(expression);
        while (matcher.find()) {
            BigDecimal left = new BigDecimal(matcher.group(1));
            String op = matcher.group(2);
            BigDecimal right = new BigDecimal(matcher.group(3));
            BigDecimal result = op.equals("*") ? left.multiply(right) : left.divide(right, 10, RoundingMode.HALF_UP);
            expression = expression.replace(matcher.group(0), result.toString());
            matcher = mdPattern.matcher(expression);
        }

        // 处理加减
        java.util.regex.Pattern asPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*([+-])\\s*(\\d+\\.?\\d*)");
        matcher = asPattern.matcher(expression);
        while (matcher.find()) {
            BigDecimal left = new BigDecimal(matcher.group(1));
            String op = matcher.group(2);
            BigDecimal right = new BigDecimal(matcher.group(3));
            BigDecimal result = op.equals("+") ? left.add(right) : left.subtract(right);
            expression = expression.replace(matcher.group(0), result.toString());
            matcher = asPattern.matcher(expression);
        }

        return new BigDecimal(expression);
    }
}
