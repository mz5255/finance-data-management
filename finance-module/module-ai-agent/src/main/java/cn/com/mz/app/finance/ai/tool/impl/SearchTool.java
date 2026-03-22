package cn.com.mz.app.finance.ai.tool.impl;

import cn.com.mz.app.finance.ai.tool.Tool;
import cn.com.mz.app.finance.ai.tool.ToolResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索工具
 * 执行简单的搜索功能
 *
 * @author mz
 */
@Component
public class SearchTool implements Tool {

    @Override
    public String getName() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "执行搜索查询，返回相关结果";
    }

    @Override
    public String getCategory() {
        return "utility";
    }

    @Override
    public RiskLevel getRiskLevel() {
        return RiskLevel.LOW;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("query", Map.of(
                "type", "string",
                "description", "搜索关键词"
        ));
        properties.put("limit", Map.of(
                "type", "integer",
                "description", "返回结果数量限制，默认10",
                "default", 10
        ));

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("query")
        );
    }

    @Override
    public ToolResult execute(Map<String, Object> params) {
        String query = (String) params.get("query");
        int limit = params.containsKey("limit") ? ((Number) params.get("limit")).intValue() : 10;

        if (query == null || query.isBlank()) {
            return ToolResult.error("搜索关键词不能为空");
        }

        // 模拟搜索结果
        List<Map<String, Object>> results = List.of(
                Map.of(
                        "title", "搜索结果示例 1",
                        "url", "https://example.com/1",
                        "snippet", "这是关于 " + query + " 的搜索结果..."
                ),
                Map.of(
                        "title", "搜索结果示例 2",
                        "url", "https://example.com/2",
                        "snippet", "更多关于 " + query + " 的相关信息..."
                )
        );

        return ToolResult.success(Map.of(
                "query", query,
                "total", results.size(),
                "results", results.subList(0, Math.min(results.size(), limit))
        ));
    }
}
