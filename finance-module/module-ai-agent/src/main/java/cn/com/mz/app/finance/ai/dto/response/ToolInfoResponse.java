package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 工具信息响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "工具信息响应")
public class ToolInfoResponse {

    @Schema(description = "工具名称")
    private String name;

    @Schema(description = "工具描述")
    private String description;

    @Schema(description = "工具分类")
    private String category;

    @Schema(description = "风险等级: LOW/MEDIUM/HIGH")
    private String riskLevel;

    @Schema(description = "是否需要确认")
    private Boolean requireConfirmation;

    @Schema(description = "参数定义")
    private Map<String, Object> parameters;

    @Schema(description = "使用示例")
    private List<Example> examples;

    /**
     * 使用示例
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Example {
        private String description;
        private Map<String, Object> parameters;
    }
}
