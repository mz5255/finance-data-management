package cn.com.mz.app.finance.application.webapi.ai;

import cn.com.mz.app.finance.ai.agent.Agent;
import cn.com.mz.app.finance.ai.agent.AgentFactory;
import cn.com.mz.app.finance.ai.dto.request.ChatRequest;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.ConversationService;
import cn.com.mz.app.finance.ai.service.file.AiFileProcessService;
import cn.com.mz.app.finance.common.dto.base.BaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    private final AiFileProcessService fileProcessService;
    private final ConversationService conversationService;

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

    @PostMapping(value = "/chat/with-files", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "带文件的流式对话", description = "上传文件并发送消息，AI 分析文件内容（支持图片）")
    public Flux<ChatResponse> chatWithFiles(
            @RequestParam("message") String message,
            @RequestParam(value = "conversationId", required = false) String conversationId,
            @RequestParam(value = "agentType", defaultValue = "chat") String agentType,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {

        log.info("Chat with files - agentType: {}, conversationId: {}, files: {}",
                agentType, conversationId, files != null ? files.length : 0);

        try {
            // 处理文件
            String fileContent = "";
            List<AiFileProcessService.ImageData> images = null;
            List<String> fileNames = null;

            if (files != null && files.length > 0) {
                AiFileProcessService.FileProcessResult result = fileProcessService.processFiles(files);
                fileContent = result.textContent();
                images = result.images();

                // 提取文件名
                fileNames = java.util.Arrays.stream(files)
                        .map(MultipartFile::getOriginalFilename)
                        .collect(Collectors.toList());

                log.info("Files processed - text length: {}, images: {}", fileContent.length(), images.size());
            }

            // 构建完整消息
            String fullMessage;
            if (!fileContent.isEmpty()) {
                fullMessage = message + "\n\n--- 上传的文件内容 ---" + fileContent;
            } else {
                fullMessage = message;
            }

            // 获取或创建会话
            String convId = conversationService.getOrCreateConversation(conversationId, agentType);

            // 保存用户消息（包含文件信息）
            String userMessageToSave = message;
            if (fileNames != null && !fileNames.isEmpty()) {
                userMessageToSave += " [附件: " + String.join(", ", fileNames) + "]";
            }
            conversationService.addMessage(convId, "USER", userMessageToSave);

            // 获取 Agent - 如果有图片，使用视觉模型
            Agent agent;
            if (images != null && !images.isEmpty()) {
                // 有图片，创建使用视觉模型的 Agent
                log.info("Creating agent with vision model for {} images", images.size());
                AiModuleConfig visionConfig = AiModuleConfig.builder()
                        .type(agentType)
                        .modelId("zhipu-vision")
                        .maxContextRounds(20)
                        .temperature(0.7)
                        .maxTokens(4096)
                        .timeout(60000)
                        .streamEnabled(true)
                        .build();
                agent = agentFactory.create(visionConfig);
            } else {
                // 纯文本，使用默认 Agent
                agent = agentFactory.createByType(agentType);
            }

            // 用于累积 AI 回复
            AtomicReference<StringBuilder> contentAccumulator = new AtomicReference<>(new StringBuilder());

            // 根据是否有图片选择不同的调用方式
            Flux<ChatResponse> responseFlux;
            if (images != null && !images.isEmpty()) {
                // 有图片，使用多模态调用
                log.info("Using multimodal chat with {} images", images.size());
                responseFlux = agent.chatStreamWithImages(convId, fullMessage, images);
            } else {
                // 纯文本
                responseFlux = agent.chatStream(convId, fullMessage);
            }

            return responseFlux
                    .doOnNext(response -> {
                        if (response.getContent() != null) {
                            contentAccumulator.get().append(response.getContent());
                        }
                    })
                    .doOnComplete(() -> {
                        // 保存 AI 回复
                        String aiContent = contentAccumulator.get().toString();
                        if (!aiContent.isEmpty()) {
                            conversationService.addMessage(convId, "ASSISTANT", aiContent);
                        }
                    })
                    .doOnError(error -> {
                        log.error("Chat with files error", error);
                        String partialContent = contentAccumulator.get().toString();
                        if (!partialContent.isEmpty()) {
                            conversationService.addMessage(convId, "ASSISTANT",
                                    partialContent + "\n\n[错误: 响应被中断]");
                        }
                    });

        } catch (IllegalArgumentException e) {
            log.warn("File validation failed: {}", e.getMessage());
            return Flux.just(ChatResponse.builder()
                    .content("文件处理失败: " + e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Chat with files failed", e);
            return Flux.just(ChatResponse.builder()
                    .content("处理请求时发生错误，请重试。")
                    .build());
        }
    }
}
