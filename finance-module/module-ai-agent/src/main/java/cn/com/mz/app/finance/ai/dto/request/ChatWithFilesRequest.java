package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带文件的聊天请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "带文件的聊天请求")
public class ChatWithFilesRequest {

    @Schema(description = "用户消息")
    private String message;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "Agent类型")
    @Builder.Default
    private String agentType = "chat";

    @Schema(description = "文件名列表（用于记录）")
    private java.util.List<String> fileNames;
}
