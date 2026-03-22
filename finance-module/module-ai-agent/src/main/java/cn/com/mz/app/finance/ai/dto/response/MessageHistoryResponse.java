package cn.com.mz.app.finance.ai.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息历史响应
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息历史响应")
public class MessageHistoryResponse {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "消息列表")
    private List<MessageItem> messages;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "页码")
    private Integer page;

    @Schema(description = "每页数量")
    private Integer size;

    /**
     * 消息项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageItem {
        private String messageId;
        private String role;
        private String content;
        private Object toolCalls;
        private LocalDateTime createdAt;
    }
}
