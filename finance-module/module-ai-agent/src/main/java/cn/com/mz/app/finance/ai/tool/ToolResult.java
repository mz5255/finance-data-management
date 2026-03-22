package cn.com.mz.app.finance.ai.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具执行结果
 *
 * @author mz
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 执行结果
     */
    private Object data;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 执行耗时（毫秒）
     */
    private long duration;

    public static ToolResult success(Object data) {
        return ToolResult.builder()
                .success(true)
                .data(data)
                .build();
    }

    public static ToolResult success(Object data, long duration) {
        return ToolResult.builder()
                .success(true)
                .data(data)
                .duration(duration)
                .build();
    }

    public static ToolResult error(String error) {
        return ToolResult.builder()
                .success(false)
                .error(error)
                .build();
    }

    public static ToolResult error(String error, long duration) {
        return ToolResult.builder()
                .success(false)
                .error(error)
                .duration(duration)
                .build();
    }
}
