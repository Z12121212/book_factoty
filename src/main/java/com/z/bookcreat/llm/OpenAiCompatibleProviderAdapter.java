package com.z.bookcreat.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.bookcreat.config.AppLlmProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OpenAiCompatibleProviderAdapter implements LlmProviderAdapter {

    private static final Set<String> SUPPORTED_CODES = Set.of("openai", "deepseek", "openai_compatible");

    private final AppLlmProperties appLlmProperties;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(ModelRouteResult route) {
        return SUPPORTED_CODES.contains(route.getProviderCode())
                || StringUtils.hasText(route.getBaseUrl());
    }

    @Override
    public String chat(ModelRouteResult route, LlmChatRequest request) {
        String wireApi = resolveWireApi(route);
        return switch (wireApi) {
            case "responses" -> callResponsesApi(route, request);
            case "chat_completions" -> callChatCompletionsApi(route, request);
            default -> throw new IllegalArgumentException("不支持的 wireApi: " + wireApi);
        };
    }

    private String callResponsesApi(ModelRouteResult route, LlmChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", route.getModelCode());
        body.put("input", buildInputMessages(request));
        body.put("store", false);
        body.put("max_output_tokens", resolveMaxTokens(route, request));

        Double temperature = resolveTemperature(route, request);
        if (temperature != null) {
            body.put("temperature", temperature);
        }

        JsonNode jsonNode = post(route, "/responses", body);
        String outputText = jsonNode.path("output_text").asText();
        if (StringUtils.hasText(outputText)) {
            return outputText;
        }

        JsonNode output = jsonNode.path("output");
        if (output.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (content.isArray()) {
                    for (JsonNode contentItem : content) {
                        String text = contentItem.path("text").asText();
                        if (StringUtils.hasText(text)) {
                            if (!builder.isEmpty()) {
                                builder.append('\n');
                            }
                            builder.append(text);
                        }
                    }
                }
            }
            if (!builder.isEmpty()) {
                return builder.toString();
            }
        }

        throw new IllegalArgumentException("Responses API 未返回可用文本内容");
    }

    private String callChatCompletionsApi(ModelRouteResult route, LlmChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", route.getModelCode());
        body.put("messages", buildInputMessages(request));
        body.put("max_tokens", resolveMaxTokens(route, request));

        Double temperature = resolveTemperature(route, request);
        if (temperature != null) {
            body.put("temperature", temperature);
        }

        JsonNode jsonNode = post(route, "/chat/completions", body);
        JsonNode contentNode = jsonNode.path("choices").path(0).path("message").path("content");
        String content = contentNode.asText();
        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("Chat Completions 未返回可用文本内容");
        }
        return content;
    }

    private JsonNode post(ModelRouteResult route, String path, Map<String, Object> body) {
        RestClient client = RestClient.builder()
                .baseUrl(normalizeBaseUrl(route.getBaseUrl()))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + route.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String responseBody = client.post()
                .uri(path)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception ex) {
            throw new IllegalArgumentException("模型响应不是合法 JSON: " + responseBody, ex);
        }
    }

    private List<Map<String, String>> buildInputMessages(LlmChatRequest request) {
        return List.of(
                Map.of("role", "system", "content", request.getSystemPrompt()),
                Map.of("role", "user", "content", request.getUserPrompt())
        );
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalArgumentException("baseUrl 不能为空");
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String resolveWireApi(ModelRouteResult route) {
        if (StringUtils.hasText(route.getMetadataJson())) {
            try {
                JsonNode metadata = objectMapper.readTree(route.getMetadataJson());
                String wireApi = metadata.path("wireApi").asText();
                if (StringUtils.hasText(wireApi)) {
                    return wireApi;
                }
            } catch (Exception ignored) {
            }
        }
        return "responses";
    }

    private Double resolveTemperature(ModelRouteResult route, LlmChatRequest request) {
        BigDecimal value = request.getTemperatureOverride() != null ? request.getTemperatureOverride() : route.getTemperature();
        return value == null ? null : value.doubleValue();
    }

    private Integer resolveMaxTokens(ModelRouteResult route, LlmChatRequest request) {
        Integer value = request.getMaxTokensOverride() != null ? request.getMaxTokensOverride() : route.getMaxTokens();
        return value == null ? 4000 : value;
    }
}
