package cn.com.mz.app.finance.ai.tool;

import java.util.List;
import java.util.Map;

/**
 * Tool 接口
 * 定义工具的基本行为
 *
 * @author mz
 */
public interface Tool {

    /**
     * 获取工具名称
     * @return 工具名称
     */
    String getName();

    /**
     * 获取工具描述
     * @return 工具描述
     */
    String getDescription();

    /**
     * 获取工具分类
     * @return 分类名称
     */
    String getCategory();

    /**
     * 获取风险等级
     * @return 风险等级: LOW/MEDIUM/HIGH
     */
    RiskLevel getRiskLevel();

    /**
     * 是否需要用户确认
     * @return 是否需要确认
     */
    default boolean requireConfirmation() {
        return getRiskLevel() == RiskLevel.HIGH;
    }

    /**
     * 获取参数定义
     * @return 参数 Schema
     */
    Map<String, Object> getParameters();

    /**
     * 获取使用示例
     * @return 示例列表
     */
    default List<Map<String, Object>> getExamples() {
        return List.of();
    }

    /**
     * 执行工具
     * @param params 执行参数
     * @return 执行结果
     */
    ToolResult execute(Map<String, Object> params);

    /**
     * 风险等级枚举
     */
    enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }
}
