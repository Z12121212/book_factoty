package com.z.bookcreat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.bookcreat.dto.IdeaGenerateRequest;
import com.z.bookcreat.dto.IdeaGenerateResponse;
import com.z.bookcreat.dto.IdeaOption;
import com.z.bookcreat.llm.LlmChatRequest;
import com.z.bookcreat.llm.LlmChatResponse;
import com.z.bookcreat.service.IdeaGenerationService;
import com.z.bookcreat.service.LlmInvokeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeaGenerationServiceImpl implements IdeaGenerationService {

    private final LlmInvokeService llmInvokeService;
    private final ObjectMapper objectMapper;

    @Override
    public IdeaGenerateResponse generate(IdeaGenerateRequest request) {
        LlmChatResponse response = llmInvokeService.chat(LlmChatRequest.builder()
                .novelId(request.getNovelId())
                .sceneCode(request.getSceneCode())
                .systemPrompt(buildSystemPrompt(request.getCount()))
                .userPrompt(buildUserPrompt(request))
                .build());

        String normalized = normalizeJson(response.getContent());
        List<IdeaOption> ideas = parseIdeas(normalized);
        return IdeaGenerateResponse.builder()
                .ideas(ideas)
                .route(response.getRoute())
                .rawContent(response.getContent())
                .build();
    }

    private String buildSystemPrompt(Integer count) {
        return """
                You are a Chinese web novel planning editor.
                Generate multiple creative directions for a long-form serialized novel.
                Output must be valid JSON only. Do not output Markdown.
                JSON format:
                {"ideas":[{"title":"idea title","summary":"80 to 160 Chinese characters summary","sellingPoint":"one sentence selling point"}]}
                The ideas array size must be exactly %d.
                All returned titles, summaries, and selling points must be in Simplified Chinese.
                """.formatted(count);
    }

    private String buildUserPrompt(IdeaGenerateRequest request) {
        String userIdea = StringUtils.hasText(request.getUserIdea()) ? request.getUserIdea() : "No extra idea, generate from genre only.";
        return """
                Genre: %s
                User idea: %s

                Requirements:
                1. Suitable for Chinese long-form web novel serialization.
                2. Strong commercial hook and clear differentiation.
                3. Each idea should be clearly different from the others.
                4. Preserve uniqueness and avoid shallow template copying.
                """.formatted(request.getGenreName(), userIdea);
    }

    private List<IdeaOption> parseIdeas(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode ideasNode = root.path("ideas");
            if (!ideasNode.isArray()) {
                throw new IllegalArgumentException("模型返回缺少 ideas 数组");
            }
            List<IdeaOption> ideas = new ArrayList<>();
            for (JsonNode node : ideasNode) {
                IdeaOption option = new IdeaOption();
                option.setTitle(node.path("title").asText());
                option.setSummary(node.path("summary").asText());
                option.setSellingPoint(node.path("sellingPoint").asText());
                ideas.add(option);
            }
            return ideas;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("模型返回不是合法 JSON: " + ex.getOriginalMessage());
        }
    }

    private String normalizeJson(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```json", "")
                    .replaceFirst("^```", "")
                    .replaceFirst("```$", "")
                    .trim();
        }
        return trimmed;
    }
}
