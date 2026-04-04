package cn.com.mz.app.finance.ai.model.impl;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.exception.AgentException;
import cn.com.mz.app.finance.ai.model.LlmModel;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import cn.com.mz.app.finance.ai.service.file.AiFileProcessService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 智谱 GLM 模型实现
 * 支持文本和多模态（图片）输入
 *
 * @author mz
 */
@Slf4j
public class ZhipuModel implements LlmModel {

    private static final String API_PATH = "/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson GSON = new Gson();

    private final AiProperties.ModelConfig config;
    private final OkHttpClient httpClient;
    private final String apiUrl;

    public ZhipuModel(AiProperties.ModelConfig config) {
        this.config = config;
        this.apiUrl = config.getBaseUrl().replaceAll("/$", "") + API_PATH;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        log.info("ZhipuModel initialized - model: {}, apiUrl: {}", config.getModel(), apiUrl);
    }

    @Override
    public String getModelId() {
        return "zhipu";
    }

    @Override
    public String getModelName() {
        return "智谱 GLM";
    }

    @Override
    public ChatResponse chat(String conversationId, String message, String systemPrompt,
                             List<String> availableTools, AiModuleConfig config) {
        JsonObject requestBody = buildRequestBody(message, null, systemPrompt, availableTools, false);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + this.config.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw AgentException.modelCallFailed("HTTP " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            return parseResponse(responseBody);

        } catch (IOException e) {
            throw AgentException.modelCallFailed(e.getMessage(), e);
        }
    }

    /**
     * 带图片的聊天
     */
    public ChatResponse chatWithImages(String conversationId, String message,
                                       List<AiFileProcessService.ImageData> images,
                                       String systemPrompt, List<String> availableTools,
                                       AiModuleConfig config) {
        JsonObject requestBody = buildRequestBody(message, images, systemPrompt, availableTools, false);

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + this.config.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw AgentException.modelCallFailed("HTTP " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            return parseResponse(responseBody);

        } catch (IOException e) {
            throw AgentException.modelCallFailed(e.getMessage(), e);
        }
    }

    @Override
    public Flux<ChatResponse> chatStream(String conversationId, String message, String systemPrompt,
                                          List<String> availableTools, AiModuleConfig config) {
        return chatStreamWithImages(conversationId, message, null, systemPrompt, availableTools, config);
    }

    /**
     * 实现接口的多模态对话方法
     */
    @Override
    public Flux<ChatResponse> chatStreamWithImages(String conversationId, String message,
                                                   List<ImageContent> images, String systemPrompt,
                                                   List<String> availableTools, AiModuleConfig config) {
        // 转换图片类型
        List<AiFileProcessService.ImageData> imageDataList = null;
        if (images != null && !images.isEmpty()) {
            imageDataList = images.stream()
                    .map(img -> new AiFileProcessService.ImageData(
                            "image", img.base64Data(), img.mimeType()))
                    .toList();
        }
        return chatStreamWithImagesInternal(conversationId, message, imageDataList, systemPrompt, availableTools, config);
    }

    /**
     * 带图片的流式聊天（内部实现）
     */
    public Flux<ChatResponse> chatStreamWithImagesInternal(String conversationId, String message,
                                                           List<AiFileProcessService.ImageData> images,
                                                           String systemPrompt, List<String> availableTools,
                                                           AiModuleConfig config) {
        return Flux.create(sink -> {
            JsonObject requestBody = buildRequestBody(message, images, systemPrompt, availableTools, true);
            String requestBodyStr = requestBody.toString();

            // 调试日志：输出请求信息（截断 base64 数据以便阅读）
            String logBody = requestBodyStr;
            if (images != null && !images.isEmpty() && logBody.length() > 1000) {
                logBody = logBody.substring(0, 500) + "...[truncated]..." + logBody.substring(logBody.length() - 200);
            }
            log.info("Sending request to API: {}, model: {}", apiUrl, this.config.getModel());
            log.debug("Request body: {}", logBody);

            // 对于多模态请求，先发送非流式请求验证格式
            if (images != null && !images.isEmpty()) {
                try {
                    JsonObject testBody = buildRequestBody(message, images, systemPrompt, availableTools, false);

                    // 打印请求体结构（不含完整 base64）- 使用 System.out 确保能看到
                    String bodyStr = testBody.toString();
                    String logStr;
                    if (bodyStr.length() > 500) {
                        logStr = bodyStr.substring(0, 300) + "...[TRUNCATED]..." + bodyStr.substring(bodyStr.length() - 100);
                    } else {
                        logStr = bodyStr;
                    }
                    System.out.println("===== Pre-flight request body =====");
                    System.out.println(logStr);
                    System.out.println("===================================");
                    log.info("Pre-flight request body (truncated): {}", logStr);

                    Request testRequest = new Request.Builder()
                            .url(apiUrl)
                            .header("Authorization", "Bearer " + this.config.getApiKey())
                            .header("Content-Type", "application/json")
                            .post(RequestBody.create(testBody.toString(), JSON))
                            .build();

                    try (Response testResponse = httpClient.newCall(testRequest).execute()) {
                        if (!testResponse.isSuccessful()) {
                            String errorBody = testResponse.body() != null ? testResponse.body().string() : "No body";
                            log.error("API request failed - HTTP {}: {}", testResponse.code(), errorBody);
                            sink.error(AgentException.modelCallFailed("HTTP " + testResponse.code() + ": " + errorBody));
                            return;
                        }
                        log.info("Non-streaming test passed, proceeding with SSE stream");
                    }
                } catch (Exception e) {
                    log.error("Pre-flight request failed: {}", e.getMessage(), e);
                    sink.error(AgentException.modelCallFailed("Pre-flight check failed: " + e.getMessage(), e));
                    return;
                }
            }

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + this.config.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .post(RequestBody.create(requestBodyStr, JSON))
                    .build();

            EventSource.Factory factory = EventSources.createFactory(httpClient);

            factory.newEventSource(request, new EventSourceListener() {
                private StringBuilder contentBuilder = new StringBuilder();
                private String messageId;

                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    if ("[DONE]".equals(data)) {
                        sink.complete();
                        return;
                    }

                    try {
                        JsonObject json = GSON.fromJson(data, JsonObject.class);
                        JsonObject delta = json.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("delta");

                        if (delta.has("content")) {
                            String content = delta.get("content").getAsString();
                            contentBuilder.append(content);

                            ChatResponse response = ChatResponse.builder()
                                    .messageId(messageId != null ? messageId : UUID.randomUUID().toString())
                                    .content(content)
                                    .build();

                            sink.next(response);
                        }

                        if (json.has("id")) {
                            messageId = json.get("id").getAsString();
                        }

                    } catch (Exception e) {
                        log.error("Error parsing stream event: {}", e.getMessage());
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    if (!sink.isCancelled()) {
                        sink.complete();
                    }
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    String errorMsg = "Stream failed";
                    if (t != null) {
                        errorMsg = t.getMessage();
                        log.error("Stream failed with throwable: {}", errorMsg, t);
                    } else if (response != null) {
                        try {
                            String errorBody = response.body() != null ? response.body().string() : "No body";
                            errorMsg = "HTTP " + response.code() + ": " + response.message() + " - " + errorBody;
                            log.error("Stream failed with response: {}", errorMsg);
                        } catch (IOException e) {
                            errorMsg = "HTTP " + response.code() + ": " + response.message();
                            log.error("Stream failed: {}", errorMsg);
                        }
                    }
                    if (!sink.isCancelled()) {
                        sink.error(AgentException.modelCallFailed(errorMsg, t));
                    }
                }
            });

        }, FluxSink.OverflowStrategy.BUFFER);
    }

    @Override
    public float[] embed(String text) {
        log.warn("Embed not implemented for ZhipuModel, returning empty array");
        return new float[1024];
    }

    @Override
    public int countTokens(String text) {
        return (int) (text.length() * 1.5);
    }

    @Override
    public boolean isAvailable() {
        return config != null && config.getApiKey() != null && !config.getApiKey().isBlank();
    }

    /**
     * 构建请求体
     *
     * @param message        文本消息
     * @param images         图片列表（可选）
     * @param systemPrompt   系统提示词
     * @param availableTools 可用工具
     * @param stream         是否流式
     */
    private JsonObject buildRequestBody(String message, List<AiFileProcessService.ImageData> images,
                                        String systemPrompt, List<String> availableTools, boolean stream) {
        JsonObject body = new JsonObject();
        body.addProperty("model", config.getModel());
        body.addProperty("stream", stream);
        body.addProperty("max_tokens", config.getMaxTokens());
        body.addProperty("temperature", config.getTemperature());

        // 消息列表
        JsonArray messagesArray = new JsonArray();

        // 对于多模态请求，按照官方文档格式构建
        // 参考：https://open.bigmodel.cn/dev/api/normal-model/glm-4v
        if (images != null && !images.isEmpty()) {
            // 多模态消息：content 是数组，图片在前，文本在后
            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");

            JsonArray contentArray = new JsonArray();

            // 先添加图片（官方文档格式：图片在前）
            for (AiFileProcessService.ImageData image : images) {
                // 检查图片大小（base64 字符串长度 / 1.33 ≈ 原始字节数，5MB 限制）
                int base64Length = image.base64Data().length();
                long estimatedSizeMB = (long) (base64Length / 1.33) / (1024 * 1024);
                System.out.println("Image: " + image.fileName() + ", base64 length: " + base64Length + ", estimated size: " + estimatedSizeMB + "MB");

                JsonObject imageContent = new JsonObject();
                imageContent.addProperty("type", "image_url");
                JsonObject imageUrl = new JsonObject();
                // GLM-4V 的 base64 格式：直接使用 base64 字符串，不需要 data URI 前缀
                imageUrl.addProperty("url", image.base64Data());
                imageContent.add("image_url", imageUrl);
                contentArray.add(imageContent);
            }

            // 添加用户文本（多模态请求不包含系统提示词，避免格式问题）
            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            // 简化文本，只发送用户消息
            textContent.addProperty("text", message);
            contentArray.add(textContent);

            userMessage.add("content", contentArray);
            messagesArray.add(userMessage);

            log.debug("Multimodal request - images: {}, text length: {}", images.size(), fullText.length());
        } else {
            // 纯文本消息：可以使用 system 消息
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", systemPrompt);
                messagesArray.add(systemMessage);
            }

            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", message);
            messagesArray.add(userMessage);
        }

        body.add("messages", messagesArray);

        return body;
    }

    private ChatResponse parseResponse(String responseBody) {
        JsonObject json = GSON.fromJson(responseBody, JsonObject.class);

        JsonObject choice = json.getAsJsonArray("choices").get(0).getAsJsonObject();
        JsonObject message = choice.getAsJsonObject("message");
        String content = message.get("content").getAsString();

        JsonObject usage = json.getAsJsonObject("usage");

        return ChatResponse.builder()
                .messageId(json.get("id").getAsString())
                .content(content)
                .tokenUsage(ChatResponse.TokenUsage.builder()
                        .inputTokens(usage.get("prompt_tokens").getAsInt())
                        .outputTokens(usage.get("completion_tokens").getAsInt())
                        .totalTokens(usage.get("total_tokens").getAsInt())
                        .build())
                .build();
    }
}
