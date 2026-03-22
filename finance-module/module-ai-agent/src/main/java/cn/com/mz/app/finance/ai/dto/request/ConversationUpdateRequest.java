package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新会话请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新会话请求")
public class ConversationUpdateRequest {

    @Schema(description = "会话标题")
    private String title;
}
