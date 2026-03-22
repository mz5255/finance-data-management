package cn.com.mz.app.finance.ai.service;

import cn.com.mz.app.finance.ai.dto.response.ConversationResponse;
import cn.com.mz.app.finance.ai.dto.response.MessageHistoryResponse;

import java.util.List;

/**
 * 会话服务接口
 *
 * @author mz
 */
public interface ConversationService {

    /**
     * 获取或创建会话
     */
    String getOrCreateConversation(String conversationId, String agentType);

    /**
     * 创建新会话
     */
    ConversationResponse createConversation(String title, String agentType, String modelId);

    /**
     * 获取会话详情
     */
    ConversationResponse getConversation(String conversationId);

    /**
     * 获取会话列表
     */
    List<ConversationResponse> getConversations(String status, String agentType, int page, int size);

    /**
     * 删除会话
     */
    void deleteConversation(String conversationId);

    /**
     * 添加消息
     */
    void addMessage(String conversationId, String role, String content);

    /**
     * 获取消息历史
     */
    MessageHistoryResponse getMessageHistory(String conversationId, int page, int size, String order);

    /**
     * 获取会话的消息列表
     */
    List<MessageHistoryResponse.MessageItem> getMessages(String conversationId, int limit);

    /**
     * 更新会话标题
     */
    void updateConversationTitle(String conversationId, String title);
}
