package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 确认响应请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "确认响应请求")
public class ConfirmationResponseRequest {

    @Schema(description = "是否批准", required = true)
    private Boolean approved;

    @Schema(description = "备注")
    private String comment;
}
