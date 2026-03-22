package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Token 使用统计响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 使用统计响应")
public class TokenUsageResponse {

    @Schema(description = "总输入Token数")
    private Long totalInputTokens;

    @Schema(description = "总输出Token数")
    private Long totalOutputTokens;

    @Schema(description = "总Token数")
    private Long totalTokens;

    @Schema(description = "总成本")
    private Double totalCost;

    @Schema(description = "每日使用情况")
    private List<DailyUsage> dailyUsage;

    @Schema(description = "模型使用分布")
    private List<ModelUsage> modelBreakdown;

    /**
     * 每日使用
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyUsage {
        private String date;
        private Long inputTokens;
        private Long outputTokens;
        private Long totalTokens;
        private Double cost;
    }

    /**
     * 模型使用
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelUsage {
        private String modelId;
        private Long totalTokens;
        private Double cost;
    }
}
