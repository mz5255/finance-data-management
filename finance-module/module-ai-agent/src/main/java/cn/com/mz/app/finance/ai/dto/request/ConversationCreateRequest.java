package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建会话请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建会话请求")
public class ConversationCreateRequest {

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "Agent类型，默认chat")
    @Builder.Default
    private String agentType = "chat";

    @Schema(description = "指定模型ID")
    private String modelId;
}
