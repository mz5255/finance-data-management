package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 健康检查响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康检查响应")
public class HealthCheckResponse {

    @Schema(description = "状态: UP/DOWN")
    private String status;

    @Schema(description = "组件状态")
    private Map<String, ComponentStatus> components;

    /**
     * 组件状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentStatus {
        private String status;
        private String modelId;
        private Long latency;
        private Integer documentCount;
    }
}
