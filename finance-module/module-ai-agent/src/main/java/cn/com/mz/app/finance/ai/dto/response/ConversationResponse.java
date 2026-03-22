package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会话响应")
public class ConversationResponse {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "Agent类型")
    private String agentType;

    @Schema(description = "模型ID")
    private String modelId;

    @Schema(description = "消息数量")
    private Integer messageCount;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageAt;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
