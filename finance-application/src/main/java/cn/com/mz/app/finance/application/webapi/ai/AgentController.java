package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.agent.strategy.AgentStrategy;
import cn.com.mz.app.finance.ai.dto.response.AgentInfoResponse;
import cn.com.mz.app.finance.ai.agent.AgentFactory;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Agent 管理控制器
 *
 * @author mz
 */
@Slf4j
@RestController
@RequestMapping("/api/finance-data/ai/agents")
@RequiredArgsConstructor
@Tag(name = "Agent 管理接口", description = "提供 Agent 相关的 API")
public class AgentController {

    private final AgentFactory agentFactory;

    @GetMapping
    @Operation(summary = "获取可用 Agent 列表", description = "获取系统中所有可用的 Agent 类型")
    public BaseResult<Map<String, Object>> getAgents() {
        List<String> types = agentFactory.getSupportedTypes();

        List<AgentInfoResponse> agents = types.stream()
                .map(type -> {
                    AgentStrategy strategy = agentFactory.getStrategy(type);
                    return AgentInfoResponse.builder()
                            .type(type)
                            .name(strategy != null ? strategy.getName() : type)
                            .description(strategy != null ? strategy.getDescription() : "")
                            .tools(strategy != null ?
                                    strategy.getAvailableTools().stream()
                                            .map(tool -> AgentInfoResponse.ToolInfo.builder()
                                                    .name(tool)
                                                    .build())
                                            .toList() : List.of())
                            .icon(strategy != null ? strategy.getIcon() : "agent-icon")
                            .build();
                })
                .toList();

        return BaseResult.success(Map.of("agents", agents));
    }

    @GetMapping("/{agentType}")
    @Operation(summary = "获取 Agent 详情", description = "获取指定类型 Agent 的详细信息")
    public BaseResult<AgentInfoResponse> getAgent(@PathVariable String agentType) {
        AgentStrategy strategy = agentFactory.getStrategy(agentType);
        if (strategy == null) {
            return BaseResult.error(404, "Agent not found: " + agentType);
        }

        AgentInfoResponse response = AgentInfoResponse.builder()
                .type(agentType)
                .name(strategy.getName())
                .description(strategy.getDescription())
                .systemPrompt(strategy.getSystemPrompt())
                .tools(strategy.getAvailableTools().stream()
                        .map(tool -> AgentInfoResponse.ToolInfo.builder()
                                .name(tool)
                                .build())
                        .toList())
                .config(Map.of(
                        "maxContextRounds", 20,
                        "defaultModel", "zhipu"
                ))
                .icon(strategy.getIcon())
                .build();

        return BaseResult.success(response);
    }
}
