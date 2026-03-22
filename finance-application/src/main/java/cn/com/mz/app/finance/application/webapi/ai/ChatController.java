package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.AgentFactory;
import cn.com.mz.app.finance.ai.dto.request.ChatRequest;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI 对话控制器
 *
 * @author mz
 */
@Slf4j
@RestController
@RequestMapping("/api/finance-data/ai")
@RequiredArgsConstructor
@Tag(name = "AI 对话接口", description = "提供 AI 对话相关的 API")
public class ChatController {

    private final AgentFactory agentFactory;

    @PostMapping("/chat")
    @Operation(summary = "发送消息", description = "发送消息给 AI Agent，支持流式和非流式响应")
    public BaseResult<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Chat request - agentType: {}, conversationId: {}", request.getAgentType(), request.getConversationId());

        Agent agent = agentFactory.createByType(request.getAgentType());
        ChatResponse response = agent.chat(request.getConversationId(), request.getMessage());

        return BaseResult.success(response);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话", description = "发送消息并获取流式响应")
    public Flux<ChatResponse> chatStream(@RequestBody ChatRequest request) {
        log.info("Chat stream request - agentType: {}, conversationId: {}", request.getAgentType(), request.getConversationId());

        Agent agent = agentFactory.createByType(request.getAgentType());
        return agent.chatStream(request.getConversationId(), request.getMessage());
    }
}
