package cn.com.mz.app.finance.datasource.mysql.entity.ai;

import cn.com.mz.app.finance.datasource.mysql.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * AI 会话实体
 *
 * @author mz
 */
@Getter
@Setter
@TableName("ai_conversations")
public class AiConversationDO extends BaseEntity {

    /**
     * 会话唯一标识
     */
    private String conversationId;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * Agent 类型（chat/code/data）
     */
    private String agentType;

    /**
     * 模型 ID
     */
    private String modelId;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 状态（ACTIVE/DELETED）
     */
    private String status;
}
