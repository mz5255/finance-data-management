package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Agent 信息响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent 信息响应")
public class AgentInfoResponse {

    @Schema(description = "Agent 类型")
    private String type;

    @Schema(description = "Agent 名称")
    private String name;

    @Schema(description = "Agent 描述")
    private String description;

    @Schema(description = "系统提示词")
    private String systemPrompt;

    @Schema(description = "可用工具列表")
    private List<ToolInfo> tools;

    @Schema(description = "配置信息")
    private Map<String, Object> config;

    @Schema(description = "图标")
    private String icon;

    /**
     * 工具信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolInfo {
        private String name;
        private String description;
        private Object parameters;
    }
}
