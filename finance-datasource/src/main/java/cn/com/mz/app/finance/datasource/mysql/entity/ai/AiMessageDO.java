package cn.com.mz.app.finance.datasource.mysql.entity.ai;

import cn.com.mz.app.finance.datasource.mysql.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Getter;
import lombok.Setter;

/**
 * AI 消息实体
 *
 * @author mz
 */
@Getter
@Setter
@TableName(value = "ai_messages", autoResultMap = true)
public class AiMessageDO extends BaseEntity {

    /**
     * 消息唯一标识
     */
    private String messageId;

    /**
     * 关联会话 ID
     */
    private String conversationId;

    /**
     * 角色（user/assistant/system）
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 工具调用信息（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object toolCalls;
}
