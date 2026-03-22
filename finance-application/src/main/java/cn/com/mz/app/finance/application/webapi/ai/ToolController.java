package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.dto.response.ToolInfoResponse;
import cn.com.mz.app.finance.ai.tool.Tool;
import cn.com.mz.app.finance.ai.tool.ToolExecutor;
import cn.com.mz.app.finance.ai.tool.ToolRegistry;
import cn.com.mz.app.finance.ai.tool.ToolResult;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 工具管理控制器
 *
 * @author mz
 */
@Slf4j
@RestController
@RequestMapping("/api/finance-data/ai/tools")
@RequiredArgsConstructor
@Tag(name = "工具管理接口", description = "提供工具相关的 API")
public class ToolController {

    private final ToolRegistry toolRegistry;
    private final ToolExecutor toolExecutor;

    @GetMapping
    @Operation(summary = "获取可用工具列表", description = "获取系统中所有可用的工具")
    public BaseResult<Map<String, Object>> getTools(
            @RequestParam(required = false) String category) {

        List<Tool> tools = category != null
                ? toolRegistry.getToolsByCategory(category)
                : List.copyOf(toolRegistry.getAllTools());

        List<ToolInfoResponse> toolInfos = tools.stream()
                .map(this::toToolInfo)
                .toList();

        return BaseResult.success(Map.of("tools", toolInfos));
    }

    @GetMapping("/{toolName}")
    @Operation(summary = "获取工具详情", description = "获取指定工具的详细信息")
    public BaseResult<ToolInfoResponse> getTool(@PathVariable String toolName) {
        Tool tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            return BaseResult.error(404, "Tool not found: " + toolName);
        }
        return BaseResult.success(toToolInfo(tool));
    }

    @PostMapping("/{toolName}/execute")
    @Operation(summary = "执行工具", description = "手动执行指定工具")
    public BaseResult<ToolResult> executeTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> params) {

        log.info("Manual tool execution: {} with params: {}", toolName, params.keySet());
        ToolResult result = toolExecutor.execute(toolName, params);
        return BaseResult.success(result);
    }

    private ToolInfoResponse toToolInfo(Tool tool) {
        return ToolInfoResponse.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .category(tool.getCategory())
                .riskLevel(tool.getRiskLevel().name())
                .requireConfirmation(tool.requireConfirmation())
                .parameters(tool.getParameters())
                .build();
    }
}
