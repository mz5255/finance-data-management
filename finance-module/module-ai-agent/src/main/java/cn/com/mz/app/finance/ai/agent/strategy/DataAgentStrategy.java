package cn.com.mz.app.finance.ai.agent.strategy;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.impl.DataAgent;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据分析策略
 * 适合财务数据分析和报表生成
 *
 * @author mz
 */
@Component
@RequiredArgsConstructor
public class DataAgentStrategy implements AgentStrategy {

    private final ModelService modelService;
    private final ConversationService conversationService;

    @Override
    public String getName() {
        return "data";
    }

    @Override
    public String getDescription() {
        return "财务数据分析和报表生成 Agent";
    }

    @Override
    public Agent createAgent(AiModuleConfig config) {
        return new DataAgent(
                config,
                getSystemPrompt(),
                getAvailableTools(),
                modelService,
                conversationService
        );
    }

    @Override
    public String getSystemPrompt() {
        return """
            你是一个专业的财务数据分析助手。
            你可以：
            1. 查询和分析财务数据
            2. 生成财务报表
            3. 进行数据可视化
            4. 发现数据异常
            5. 提供财务建议

            在分析数据时，请注意：
            - 确保数据的准确性和完整性
            - 识别异常数据和趋势
            - 提供清晰的分析结论
            - 必要时生成可视化图表
            """;
    }

    @Override
    public List<String> getAvailableTools() {
        return List.of("database_query", "calculator", "chart_generator", "report_generator");
    }

    @Override
    public String getIcon() {
        return "data-icon";
    }
}
