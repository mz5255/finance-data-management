package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送消息请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "发送消息请求")
public class ChatRequest {

    @Schema(description = "用户消息内容", required = true)
    private String message;

    @Schema(description = "会话ID，不传则创建新会话")
    private String conversationId;

    @Schema(description = "Agent类型: chat(默认)/code/data")
    @Builder.Default
    private String agentType = "chat";

    @Schema(description = "指定模型ID，不传使用默认")
    private String modelId;

    @Schema(description = "是否流式输出，默认true")
    @Builder.Default
    private Boolean stream = true;
}
