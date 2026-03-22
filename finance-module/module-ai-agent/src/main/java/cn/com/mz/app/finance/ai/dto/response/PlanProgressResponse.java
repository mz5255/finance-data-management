package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 任务计划进度响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任务计划进度响应")
public class PlanProgressResponse {

    @Schema(description = "计划ID")
    private String planId;

    @Schema(description = "目标")
    private String goal;

    @Schema(description = "状态: PENDING/RUNNING/COMPLETED/ERROR")
    private String status;

    @Schema(description = "当前步骤")
    private Integer currentStep;

    @Schema(description = "总步骤数")
    private Integer totalSteps;

    @Schema(description = "完成百分比")
    private Integer percentage;

    @Schema(description = "子任务列表")
    private List<SubTask> subTasks;

    @Schema(description = "文件变更列表")
    private List<FileChange> files;

    /**
     * 子任务
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {
        private String taskId;
        private String description;
        private String status;
        private String result;
    }

    /**
     * 文件变更
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileChange {
        private String path;
        private String type;
        private String status;
    }
}
