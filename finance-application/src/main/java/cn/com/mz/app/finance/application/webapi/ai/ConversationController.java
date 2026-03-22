package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.dto.request.ConversationCreateRequest;
import cn.com.mz.app.finance.ai.dto.request.ConversationUpdateRequest;
import cn.com.mz.app.finance.ai.dto.request.MessageHistoryQueryRequest;
import cn.com.mz.app.finance.ai.dto.response.ConversationResponse;
import cn.com.mz.app.finance.ai.dto.response.MessageHistoryResponse;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会话管理控制器
 *
 * @author mz
 */
@Slf4j
@RestController
@RequestMapping("/api/finance-data/ai/conversations")
@RequiredArgsConstructor
@Tag(name = "会话管理接口", description = "提供会话相关的 API")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    @Operation(summary = "创建新会话", description = "创建新的对话会话")
    public BaseResult<ConversationResponse> createConversation(@RequestBody ConversationCreateRequest request) {
        log.info("Creating conversation - title: {}, agentType: {}", request.getTitle(), request.getAgentType());
        ConversationResponse response = conversationService.createConversation(
                request.getTitle(),
                request.getAgentType(),
                request.getModelId()
        );
        return BaseResult.success(response);
    }

    @GetMapping
    @Operation(summary = "获取会话列表", description = "获取当前用户的会话列表")
    public BaseResult<Map<String, Object>> getConversations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String agentType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ConversationResponse> conversations = conversationService.getConversations(status, agentType, page, size);
        return BaseResult.success(Map.of(
                "conversations", conversations,
                "page", page,
                "size", size
        ));
    }

    @GetMapping("/{conversationId}")
    @Operation(summary = "获取会话详情", description = "获取指定会话的详细信息")
    public BaseResult<ConversationResponse> getConversation(@PathVariable String conversationId) {
        ConversationResponse response = conversationService.getConversation(conversationId);
        return BaseResult.success(response);
    }

    @DeleteMapping("/{conversationId}")
    @Operation(summary = "删除会话", description = "删除指定会话（软删除）")
    public BaseResult<Void> deleteConversation(@PathVariable String conversationId) {
        conversationService.deleteConversation(conversationId);
        return BaseResult.success();
    }

    @PutMapping("/{conversationId}")
    @Operation(summary = "更新会话", description = "更新指定会话的信息（如标题）")
    public BaseResult<Void> updateConversation(
            @PathVariable String conversationId,
            @RequestBody ConversationUpdateRequest request) {
        conversationService.updateConversationTitle(conversationId, request.getTitle());
        return BaseResult.success();
    }

    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "获取消息历史", description = "获取指定会话的消息历史")
    public BaseResult<MessageHistoryResponse> getMessageHistory(
            @PathVariable String conversationId,
            MessageHistoryQueryRequest request) {

        MessageHistoryResponse response = conversationService.getMessageHistory(
                conversationId,
                request.getPage(),
                request.getSize(),
                request.getOrder()
        );
        return BaseResult.success(response);
    }
}
