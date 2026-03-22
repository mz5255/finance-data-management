package cn.com.mz.app.finance.ai.model.impl;

import cn.com.mz.app.finance.ai.config.AiProperties;
import cn.com.mz.app.finance.ai.dto.response.ChatResponse;
import cn.com.mz.app.finance.ai.exception.AgentException;
import cn.com.mz.app.finance.ai.model.LlmModel;
import cn.com.mz.app.finance.ai.module.AiModuleConfig;
import com.google.gson.Gson;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 智谱 GLM 模型实现
 *
 * @author mz
 */
@Slf4j
public class ZhipuModel implements LlmModel {

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson GSON = new Gson();

    private final AiProperties.ModelConfig config;
    private final OkHttpClient httpClient;

    public ZhipuModel(AiProperties.ModelConfig config) {
        this.config = config;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
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
        JsonObject requestBody = buildRequestBody(message, systemPrompt, availableTools, false);

        Request request = new Request.Builder()
                .url(API_URL)
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
        return Flux.create(sink -> {
            JsonObject requestBody = buildRequestBody(message, systemPrompt, availableTools, true);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + this.config.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .post(RequestBody.create(requestBody.toString(), JSON))
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
                    log.error("Stream failed: {}", t.getMessage());
                    if (!sink.isCancelled()) {
                        sink.error(AgentException.modelCallFailed(t.getMessage(), t));
                    }
                }
            });

        }, FluxSink.OverflowStrategy.BUFFER);
    }

    @Override
    public float[] embed(String text) {
        // 简化实现，实际应调用嵌入 API
        log.warn("Embed not implemented for ZhipuModel, returning empty array");
        return new float[1024];
    }

    @Override
    public int countTokens(String text) {
        // 简化实现：按字符数估算
        return (int) (text.length() * 1.5);
    }

    @Override
    public boolean isAvailable() {
        return config != null && config.getApiKey() != null && !config.getApiKey().isBlank();
    }

    private JsonObject buildRequestBody(String message, String systemPrompt, List<String> availableTools, boolean stream) {
        JsonObject body = new JsonObject();
        body.addProperty("model", config.getModel());
        body.addProperty("stream", stream);
        body.addProperty("max_tokens", config.getMaxTokens());
        body.addProperty("temperature", config.getTemperature());

        // 添加消息
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", message);

        body.add("messages", GSON.toJsonTree(List.of(systemMessage, userMessage)));

        // TODO: 添加工具定义

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
