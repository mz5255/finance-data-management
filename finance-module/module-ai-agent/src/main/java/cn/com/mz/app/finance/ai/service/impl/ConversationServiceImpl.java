package cn.com.mz.app.finance.ai.service.impl;

import cn.com.mz.app.finance.ai.dto.response.ConversationResponse;
import cn.com.mz.app.finance.ai.dto.response.MessageHistoryResponse;
import cn.com.mz.app.finance.ai.exception.AgentException;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.datasource.mysql.entity.ai.AiConversationDO;
import cn.com.mz.app.finance.datasource.mysql.entity.ai.AiMessageDO;
import cn.com.mz.app.finance.datasource.mysql.mapper.ai.AiConversationMapper;
import cn.com.mz.app.finance.datasource.mysql.mapper.ai.AiMessageMapper;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 会话服务实现
 * 使用数据库持久化存储
 *
 * @author mz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;

    /**
     * 获取当前登录用户 ID
     */
    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            log.warn("获取用户 ID 失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public String getOrCreateConversation(String conversationId, String agentType) {
        Long userId = getCurrentUserId();

        // 如果提供了 conversationId 且存在，直接返回
        if (conversationId != null) {
            LambdaQueryWrapper<AiConversationDO> query = new LambdaQueryWrapper<>();
            query.eq(AiConversationDO::getConversationId, conversationId);
            if (userId != null) {
                query.eq(AiConversationDO::getUserId, userId);
            }
            query.eq(AiConversationDO::getStatus, "ACTIVE");

            if (conversationMapper.selectCount(query) > 0) {
                return conversationId;
            }
        }

        // 创建新会话
        String newId = "conv-" + UUID.randomUUID().toString().substring(0, 8);
        AiConversationDO conversation = new AiConversationDO();
        conversation.setConversationId(newId);
        conversation.setUserId(userId);
        conversation.setAgentType(agentType);
        conversation.setStatus("ACTIVE");
        conversation.setMessageCount(0);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversation.setDeleted(0);
        conversation.setLockVersion(0);

        conversationMapper.insert(conversation);
        log.debug("Created new conversation: {}", newId);
        return newId;
    }

    @Override
    @Transactional
    public ConversationResponse createConversation(String title, String agentType, String modelId) {
        Long userId = getCurrentUserId();
        String conversationId = "conv-" + UUID.randomUUID().toString().substring(0, 8);

        AiConversationDO conversation = new AiConversationDO();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setAgentType(agentType);
        conversation.setModelId(modelId != null ? modelId : "zhipu");
        conversation.setStatus("ACTIVE");
        conversation.setMessageCount(0);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversation.setDeleted(0);
        conversation.setLockVersion(0);

        conversationMapper.insert(conversation);
        return toResponse(conversation);
    }

    @Override
    public ConversationResponse getConversation(String conversationId) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AiConversationDO> query = new LambdaQueryWrapper<>();
        query.eq(AiConversationDO::getConversationId, conversationId);
        if (userId != null) {
            query.eq(AiConversationDO::getUserId, userId);
        }

        AiConversationDO conversation = conversationMapper.selectOne(query);
        if (conversation == null) {
            throw AgentException.conversationNotFound(conversationId);
        }
        return toResponse(conversation);
    }

    @Override
    public List<ConversationResponse> getConversations(String status, String agentType, int page, int size) {
        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AiConversationDO> query = new LambdaQueryWrapper<>();
        query.eq(AiConversationDO::getDeleted, 0);

        if (userId != null) {
            query.eq(AiConversationDO::getUserId, userId);
        }
        if (status != null && !status.isEmpty()) {
            query.eq(AiConversationDO::getStatus, status);
        } else {
            query.eq(AiConversationDO::getStatus, "ACTIVE");
        }
        if (agentType != null && !agentType.isEmpty()) {
            query.eq(AiConversationDO::getAgentType, agentType);
        }

        query.orderByDesc(AiConversationDO::getUpdateTime);

        // 分页
        int offset = (page - 1) * size;
        query.last("LIMIT " + offset + ", " + size);

        List<AiConversationDO> conversations = conversationMapper.selectList(query);
        return conversations.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        Long userId = getCurrentUserId();

        LambdaUpdateWrapper<AiConversationDO> update = new LambdaUpdateWrapper<>();
        update.eq(AiConversationDO::getConversationId, conversationId);
        if (userId != null) {
            update.eq(AiConversationDO::getUserId, userId);
        }
        update.set(AiConversationDO::getStatus, "DELETED");
        update.set(AiConversationDO::getDeleted, 1);
        update.set(AiConversationDO::getUpdateTime, LocalDateTime.now());

        conversationMapper.update(null, update);
        log.debug("Deleted conversation: {}", conversationId);
    }

    @Override
    @Transactional
    public void addMessage(String conversationId, String role, String content) {
        // 添加消息
        String messageId = "msg-" + UUID.randomUUID().toString().substring(0, 8);
        AiMessageDO message = new AiMessageDO();
        message.setMessageId(messageId);
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setDeleted(0);
        message.setLockVersion(0);

        messageMapper.insert(message);

        // 更新会话信息
        LambdaQueryWrapper<AiConversationDO> query = new LambdaQueryWrapper<>();
        query.eq(AiConversationDO::getConversationId, conversationId);
        AiConversationDO conversation = conversationMapper.selectOne(query);

        if (conversation != null) {
            conversation.setMessageCount(conversation.getMessageCount() + 1);
            conversation.setUpdateTime(LocalDateTime.now());

            // 如果标题为空且是用户消息，自动设置标题
            if ((conversation.getTitle() == null || conversation.getTitle().isEmpty())
                    && "user".equalsIgnoreCase(role)) {
                String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                conversation.setTitle(title);
            }

            conversationMapper.updateById(conversation);
        }

        log.debug("Added message to conversation {}: role={}", conversationId, role);
    }

    @Override
    public MessageHistoryResponse getMessageHistory(String conversationId, int page, int size, String order) {
        // 验证会话存在
        Long userId = getCurrentUserId();
        LambdaQueryWrapper<AiConversationDO> convQuery = new LambdaQueryWrapper<>();
        convQuery.eq(AiConversationDO::getConversationId, conversationId);
        if (userId != null) {
            convQuery.eq(AiConversationDO::getUserId, userId);
        }

        AiConversationDO conversation = conversationMapper.selectOne(convQuery);
        if (conversation == null) {
            throw AgentException.conversationNotFound(conversationId);
        }

        // 查询消息
        LambdaQueryWrapper<AiMessageDO> query = new LambdaQueryWrapper<>();
        query.eq(AiMessageDO::getConversationId, conversationId);
        query.eq(AiMessageDO::getDeleted, 0);

        if ("desc".equalsIgnoreCase(order)) {
            query.orderByDesc(AiMessageDO::getCreateTime);
        } else {
            query.orderByAsc(AiMessageDO::getCreateTime);
        }

        List<AiMessageDO> allMessages = messageMapper.selectList(query);

        // 分页处理
        int offset = (page - 1) * size;
        List<MessageHistoryResponse.MessageItem> messageItems = allMessages.stream()
                .skip(offset)
                .limit(size)
                .map(this::toMessageItem)
                .toList();

        return MessageHistoryResponse.builder()
                .conversationId(conversationId)
                .title(conversation.getTitle())
                .messages(messageItems)
                .total((long) allMessages.size())
                .page(page)
                .size(size)
                .build();
    }

    @Override
    public List<MessageHistoryResponse.MessageItem> getMessages(String conversationId, int limit) {
        LambdaQueryWrapper<AiMessageDO> query = new LambdaQueryWrapper<>();
        query.eq(AiMessageDO::getConversationId, conversationId);
        query.eq(AiMessageDO::getDeleted, 0);
        query.orderByAsc(AiMessageDO::getCreateTime);

        if (limit > 0) {
            query.last("LIMIT " + limit);
        }

        List<AiMessageDO> messages = messageMapper.selectList(query);
        return messages.stream().map(this::toMessageItem).toList();
    }

    @Override
    @Transactional
    public void updateConversationTitle(String conversationId, String title) {
        Long userId = getCurrentUserId();

        LambdaUpdateWrapper<AiConversationDO> update = new LambdaUpdateWrapper<>();
        update.eq(AiConversationDO::getConversationId, conversationId);
        if (userId != null) {
            update.eq(AiConversationDO::getUserId, userId);
        }
        update.set(AiConversationDO::getTitle, title);
        update.set(AiConversationDO::getUpdateTime, LocalDateTime.now());

        conversationMapper.update(null, update);
        log.debug("Updated conversation title: {} -> {}", conversationId, title);
    }

    private ConversationResponse toResponse(AiConversationDO entity) {
        return ConversationResponse.builder()
                .conversationId(entity.getConversationId())
                .title(entity.getTitle())
                .agentType(entity.getAgentType())
                .modelId(entity.getModelId())
                .messageCount(entity.getMessageCount())
                .lastMessageAt(entity.getUpdateTime())
                .status(entity.getStatus())
                .createdAt(entity.getCreateTime())
                .build();
    }

    private MessageHistoryResponse.MessageItem toMessageItem(AiMessageDO entity) {
        return MessageHistoryResponse.MessageItem.builder()
                .messageId(entity.getMessageId())
                .role(entity.getRole())
                .content(entity.getContent())
                .toolCalls(entity.getToolCalls())
                .createdAt(entity.getCreateTime())
                .build();
    }
}
