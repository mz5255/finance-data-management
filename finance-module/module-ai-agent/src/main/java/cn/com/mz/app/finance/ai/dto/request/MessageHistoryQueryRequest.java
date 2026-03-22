package cn.com.mz.app.finance.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息历史查询请求
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息历史查询请求")
public class MessageHistoryQueryRequest {

    @Schema(description = "页码，默认1")
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每页数量，默认20")
    @Builder.Default
    private Integer size = 20;

    @Schema(description = "排序: asc/desc，默认asc")
    @Builder.Default
    private String order = "asc";
}
