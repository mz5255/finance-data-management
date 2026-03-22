package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.dto.response.HealthCheckResponse;
import cn.com.mz.app.finance.ai.model.ModelFactory;
import cn.com.mz.app.finance.ai.tool.ToolRegistry;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author mz
 */
@Slf4j
@RestController
@RequestMapping("/api/finance-data/ai")
@RequiredArgsConstructor
@Tag(name = "健康检查接口", description = "提供服务健康状态检查")
public class HealthController {

    private final ModelFactory modelFactory;
    private final ToolRegistry toolRegistry;

    @GetMapping("/health")
    @Operation(summary = "服务健康检查", description = "检查 AI 服务健康状态")
    public BaseResult<HealthCheckResponse> health() {
        Map<String, HealthCheckResponse.ComponentStatus> components = new HashMap<>();

        // 检查模型状态
        try {
            boolean modelAvailable = modelFactory.isModelAvailable("zhipu");
            components.put("model", HealthCheckResponse.ComponentStatus.builder()
                    .status(modelAvailable ? "UP" : "DOWN")
                    .modelId("zhipu")
                    .build());
        } catch (Exception e) {
            components.put("model", HealthCheckResponse.ComponentStatus.builder()
                    .status("DOWN")
                    .build());
        }

        // 检查工具状态
        try {
            int toolCount = toolRegistry.getToolNames().size();
            components.put("tools", HealthCheckResponse.ComponentStatus.builder()
                    .status("UP")
                    .documentCount(toolCount)
                    .build());
        } catch (Exception e) {
            components.put("tools", HealthCheckResponse.ComponentStatus.builder()
                    .status("DOWN")
                    .build());
        }

        String overallStatus = components.values().stream()
                .allMatch(c -> "UP".equals(c.getStatus())) ? "UP" : "DOWN";

        HealthCheckResponse response = HealthCheckResponse.builder()
                .status(overallStatus)
                .components(components)
                .build();

        return BaseResult.success(response);
    }
}
