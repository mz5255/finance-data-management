package cn.com.mz.app.finance.ai.tool;

import cn.com.mz.app.finance.ai.exception.AgentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 工具执行器
 * 负责工具的调度和执行
 *
 * @author mz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolExecutor {

    private final ToolRegistry toolRegistry;

    /**
     * 执行单个工具
     */
    public ToolResult execute(String toolName, Map<String, Object> params) {
        log.info("Executing tool: {} with params: {}", toolName, params.keySet());

        Tool tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            throw AgentException.toolNotFound(toolName);
        }

        long startTime = System.currentTimeMillis();
        try {
            // 参数校验
            validateParameters(tool, params);

            // 执行工具
            ToolResult result = tool.execute(params);
            result.setDuration(System.currentTimeMillis() - startTime);

            log.info("Tool {} executed in {}ms, success: {}", toolName, result.getDuration(), result.isSuccess());
            return result;

        } catch (AgentException e) {
            throw e;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Tool {} execution failed: {}", toolName, e.getMessage(), e);
            return ToolResult.error(e.getMessage(), duration);
        }
    }

    /**
     * 批量执行工具
     */
    public List<ToolResult> executeBatch(List<ToolExecution> executions) {
        return executions.stream()
                .map(exec -> execute(exec.toolName(), exec.params()))
                .toList();
    }

    /**
     * 检查工具是否需要确认
     */
    public boolean needsConfirmation(String toolName) {
        Tool tool = toolRegistry.getTool(toolName);
        return tool != null && tool.requireConfirmation();
    }

    /**
     * 获取工具信息
     */
    public Map<String, Object> getToolInfo(String toolName) {
        Tool tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            return null;
        }

        return Map.of(
                "name", tool.getName(),
                "description", tool.getDescription(),
                "category", tool.getCategory(),
                "riskLevel", tool.getRiskLevel().name(),
                "requireConfirmation", tool.requireConfirmation(),
                "parameters", tool.getParameters()
        );
    }

    /**
     * 参数校验
     */
    private void validateParameters(Tool tool, Map<String, Object> params) {
        Map<String, Object> schema = tool.getParameters();
        if (schema == null || schema.isEmpty()) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
        @SuppressWarnings("unchecked")
        List<String> required = (List<String>) schema.get("required");

        if (required != null && properties != null) {
            for (String requiredField : required) {
                if (!params.containsKey(requiredField)) {
                    throw new AgentException(400, String.format("缺少必填参数: %s", requiredField));
                }
            }
        }
    }

    /**
     * 工具执行记录
     */
    public record ToolExecution(String toolName, Map<String, Object> params) {}
}
